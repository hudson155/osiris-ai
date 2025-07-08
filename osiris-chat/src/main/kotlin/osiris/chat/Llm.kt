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
  private val chatRequestBlock: ChatRequest.Builder.(state: LlmState) -> Unit,
  private val toolExecutor: ToolExecutor,
  private val exitCondition: ExitCondition,
) {
  @Suppress("CognitiveComplexMethod", "NestedBlockDepth")
  suspend fun execute(): List<ChatMessage> {
    logger.debug { "Started LLM." }
    var state = LlmState()
    while (true) {
      val messages = messages + state.response
      val lastMessage = messages.lastOrNull()
      logger.debug { "Last message: ${lastMessage ?: "null"}." }
      if (exitCondition.shouldExit(state)) break
      if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
        val executionRequests = lastMessage.toolExecutionRequests()
        state = state.copy(response = state.response + executeTools(executionRequests))
      } else {
        val chatRequest = buildChatRequest(messages) { chatRequestBlock(state) }
        val aiMessage = chat(chatRequest)
        state = state.copy(
          chatRequestCount = state.chatRequestCount + 1,
          response = state.response + aiMessage,
        )
        aiMessage.text()?.let { text ->
          if (responseType == null) return@let
          try {
            llmMapper.kairoRead(text, responseType)
          } catch (e: MismatchedInputException) {
            val outputString = listOf(e.message, "Consider retrying.").joinToString("\n\n")
            state = state.copy(response = state.response + SystemMessage(outputString))
          }
        }
      }
    }
    logger.debug { "Ended LLM." }
    return state.response
  }

  private suspend fun executeTools(executionRequests: List<ToolExecutionRequest>): List<ChatMessage> {
    logger.debug { "Tool execution requests: $executionRequests." }
    val executionResults = toolExecutor.execute(tools, executionRequests)
    logger.debug { "Tool execution results: $executionResults." }
    return executionResults
  }

  private suspend fun chat(chatRequest: ChatRequest): AiMessage {
    logger.debug { "Chat request: $chatRequest." }
    val chatResponse = trace({ ChatEvent.Start(chatRequest) }, { ChatEvent.End(it) }) {
      model.chat(chatRequest)
    }
    logger.debug { "Chat response: $chatResponse." }
    return chatResponse.aiMessage()
  }

  private suspend fun buildChatRequest(
    messages: List<ChatMessage>,
    chatRequestBlock: ChatRequest.Builder.() -> Unit,
  ): ChatRequest =
    ChatRequest.builder().apply {
      messages(messages)
      if (tools.isNotEmpty()) {
        toolSpecifications(tools.map { it.toolSpecification.get() })
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
  exitCondition: ExitCondition = ExitCondition.Default(),
): List<ChatMessage> {
  require(messages.isNotEmpty()) { "Messages cannot be empty." }
  val llm = Llm(
    model = model,
    messages = messages,
    tools = tools,
    responseType = responseType,
    chatRequestBlock = chatRequestBlock,
    toolExecutor = toolExecutor,
    exitCondition = exitCondition,
  )
  return withTracer(
    tracer = tracer,
    start = { TraceEvent.Start("Trace: LLM", deriveText(messages)) },
    end = { response -> TraceEvent.End(response?.let { deriveText(it) }) },
  ) {
    llm.execute()
  }
}
