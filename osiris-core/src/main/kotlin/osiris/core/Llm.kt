package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonSchema
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
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
  private val responseType: KClass<*>?,
  private val chatRequestBlock: ChatRequest.Builder.() -> Unit,
  private val toolExecutor: ToolExecutor,
  private val exitCondition: ExitCondition,
) {
  suspend fun execute(): List<ChatMessage> {
    logger.debug { "Started LLM." }
    val response = mutableListOf<ChatMessage>()
    while (true) {
      val messages = messages + response
      val lastMessage = messages.lastOrNull()
      logger.debug { "Last message: ${lastMessage ?: "null"}." }
      if (exitCondition.shouldExit(response)) break
      if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
        val executionRequests = lastMessage.toolExecutionRequests()
        response += executeTools(executionRequests)
      } else {
        val chatRequest = buildChatRequest(messages)
        response += chat(chatRequest)
      }
    }
    logger.debug { "Ended LLM." }
    return response
  }

  private suspend fun executeTools(executionRequests: List<ToolExecutionRequest>): List<ChatMessage> {
    logger.debug { "Tool execution requests: $executionRequests." }
    val executionResults = toolExecutor.execute(tools, executionRequests)
    logger.debug { "Tool execution results: $executionResults." }
    return executionResults
  }

  private suspend fun chat(chatRequest: ChatRequest): List<ChatMessage> {
    logger.debug { "Chat request: $chatRequest." }
    val chatResponse = trace({ ChatEvent.Start(chatRequest) }, { ChatEvent.End(it) }) {
      model.chat(chatRequest)
    }
    logger.debug { "Chat response: $chatResponse." }
    val aiMessage = chatResponse.aiMessage()
    return listOf(aiMessage)
  }

  private suspend fun buildChatRequest(messages: List<ChatMessage>): ChatRequest =
    ChatRequest.builder().apply {
      messages(messages)
      if (tools.isNotEmpty()) {
        toolSpecifications(tools.map { it.toolSpecification.get() })
      }
      if (responseType != null) {
        val jsonSchema = JsonSchema.builder().apply {
          name(LlmSchema.generateName(responseType))
          rootElement(LlmSchema.generate(responseType))
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
 * executing tool calls until the LLM responds.
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
   * The LLM can consult these tools.
   */
  tools: List<Tool<*>> = emptyList(),
  /**
   * Class reference for structured output.
   * If not provided, output will be a string.
   */
  responseType: KClass<*>? = null,
  /**
   * Use this to customize the Langchain4j chat request.
   */
  chatRequestBlock: ChatRequest.Builder.() -> Unit = {},
  /**
   * Enable tracing for this request by providing a tracer.
   * If a tracer is provided but tracing is already enabled upstack, the tracer will be ignored.
   */
  tracer: Tracer? = null,
  /**
   * By default, tools are executed in parallel on [Dispatchers.IO] using [ToolExecutor.Dispatcher].
   */
  toolExecutor: ToolExecutor = ToolExecutor.Dispatcher(),
  /**
   * By default, Osiris will run LLM requests in a loop,
   * executing tool calls until the LLM responds.
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
    start = { TraceEvent.Start("Osiris", deriveText(messages)) },
    end = { TraceEvent.End(deriveText(it)) },
  ) {
    llm.execute()
  }
}
