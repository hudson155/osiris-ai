package osiris.chat

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonSchema
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kairo.reflect.KairoType
import kairo.serialization.util.kairoRead
import osiris.core.llmMapper
import osiris.schema.LlmSchema
import osiris.tracing.ChatEvent
import osiris.tracing.TraceEvent
import osiris.tracing.Tracer
import osiris.tracing.trace
import osiris.tracing.withTracer

private val logger: KLogger = KotlinLogging.logger {}

@Suppress("LongParameterList")
internal class Llm(
  private val model: ChatModel,
  private val messages: List<ChatMessage>,
  private val tools: List<Tool<*>>,
  private val responseType: KairoType<*>?,
  private val options: LlmOptions,
  private val chatRequestBlock: ChatRequest.Builder.(state: LlmState) -> Unit,
  private val toolExecutor: ToolExecutor,
  private val exitCondition: LlmExitCondition,
) {
  private var state: LlmState = LlmState()

  suspend fun execute(): List<ChatMessage> {
    logger.debug { "Started LLM." }
    while (true) {
      val lastMessage = (messages + state.response).lastOrNull()
      logger.debug { "Last message: ${lastMessage ?: "null"}." }
      if (exitCondition.shouldExit(state)) break
      if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
        val executionRequests = lastMessage.toolExecutionRequests()
        executeTools(executionRequests)
      } else {
        val chatRequest = buildChatRequest { chatRequestBlock(state) }
        chat(chatRequest)
      }
    }
    logger.debug { "Ended LLM." }
    return state.response
  }

  private suspend fun buildChatRequest(chatRequestBlock: ChatRequest.Builder.() -> Unit): ChatRequest =
    ChatRequest.builder().apply {
      messages(messages + state.response)
      if (tools.isNotEmpty()) {
        toolSpecifications(tools.filter { it.include.get() }.map { it.toolSpecification.get() })
      }
      if (responseType != null) {
        val jsonSchema = JsonSchema.builder().apply {
          name(LlmSchema.generateName(responseType.kotlinClass))
          rootElement(LlmSchema.generate(responseType.kotlinClass))
        }.build()
        val responseFormat = ResponseFormat.builder().apply {
          type(ResponseFormatType.JSON)
          jsonSchema(jsonSchema)
        }.build()
        responseFormat(responseFormat)
      }
      chatRequestBlock()
    }.build()

  private suspend fun chat(chatRequest: ChatRequest) {
    logger.debug { "Chat request: $chatRequest." }
    val chatResponse = trace({ ChatEvent.start(chatRequest) }, { ChatEvent.end(it) }) {
      model.chat(chatRequest)
    }
    logger.debug { "Chat response: $chatResponse." }
    val aiMessage = chatResponse.aiMessage()
    state = state.copy(response = state.response + aiMessage)
    aiMessage.text()?.let { text ->
      validateResponse(text)
    }
  }

  /**
   * Attempts deserialization to validate the response schema, discarding the result.
   */
  private fun validateResponse(text: String) {
    if (responseType == null) return
    state = state.copy(consecutiveResponseTries = state.consecutiveResponseTries + 1)
    try {
      llmMapper.kairoRead(text, responseType)
      state = state.copy(consecutiveResponseTries = 0)
    } catch (e: MismatchedInputException) {
      if (state.consecutiveResponseTries >= options.maxConsecutiveResponseTries) throw e
      val outputString = listOf(e.message, "Consider retrying.").joinToString("\n\n")
      state = state.copy(response = state.response + SystemMessage(outputString))
    }
  }

  private suspend fun executeTools(executionRequests: List<ToolExecutionRequest>) {
    logger.debug { "Tool execution requests: $executionRequests." }
    val executionResults = toolExecutor.execute(tools, executionRequests)
    logger.debug { "Tool execution results: $executionResults." }
    state = state.copy(response = state.response + executionResults)
  }
}

/**
 * The primary entrypoint.
 *
 * By default, Osiris will run LLM requests in a loop,
 * executing Tool calls until the LLM responds.
 *
 * For more information, refer to the documentation.
 */
@Suppress("LongParameterList")
public suspend fun llm(
  /**
   * Any Langchain4j model.
   */
  model: ChatModel,
  /**
   * The initial Langchain4j messages.
   */
  messages: List<ChatMessage>,
  /**
   * The LLM can consult these Tools.
   */
  tools: List<Tool<*>> = emptyList(),
  /**
   * Type for structured output.
   * If not provided, output will be a string.
   */
  responseType: KairoType<*>? = null,
  /**
   * The maximum number of consecutive times to try requesting an answer before throwing a fatal exception.
   * Normally only 1 try will be attempted, but response deserialization failures can cause retries.
   */
  maxConsecutiveResponseTries: Int = 3,
  /**
   * Use this to customize the Langchain4j chat request.
   */
  chatRequestBlock: ChatRequest.Builder.(state: LlmState) -> Unit = {},
  /**
   * Enable tracing for this request by providing a Tracer.
   * If a Tracer is provided but tracing is already enabled upstack, the Tracer will be ignored.
   */
  tracer: Tracer? = null,
  /**
   * By default, Tools are executed in parallel using [ToolExecutor.Dispatcher].
   */
  toolExecutor: ToolExecutor = ToolExecutor.Dispatcher(),
  /**
   * By default, Osiris will run LLM requests in a loop,
   * executing Tool calls until the LLM responds.
   */
  exitCondition: LlmExitCondition = LlmExitCondition.Default(),
): List<ChatMessage> {
  require(messages.isNotEmpty()) { "Messages cannot be empty." }
  val llm = Llm(
    model = model,
    messages = messages,
    tools = tools,
    responseType = responseType,
    options = LlmOptions(
      maxConsecutiveResponseTries = maxConsecutiveResponseTries,
    ),
    chatRequestBlock = chatRequestBlock,
    toolExecutor = toolExecutor,
    exitCondition = exitCondition,
  )
  return withTracer(
    tracer = tracer,
    buildStart = { TraceEvent.start(deriveText(messages)) },
    buildEnd = { TraceEvent.end(deriveText(it)) },
  ) {
    llm.execute()
  }
}
