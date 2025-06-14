package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonSchema
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import osiris.event.Event
import osiris.event.MessageEvent
import osiris.schema.LlmSchema

private val logger: KLogger = KotlinLogging.logger {}

@Suppress("LongParameterList")
public class Llm internal constructor(
  public val model: ChatModel,
  public val messages: List<ChatMessage>,
  public val tools: List<Tool<*>>,
  public val responseType: KClass<*>?,
  public val chatRequestBlock: ChatRequest.Builder.() -> Unit,
  public val toolExecutor: ToolExecutor,
  public val exitCondition: ExitCondition,
) {
  public val response: MutableList<ChatMessage> = mutableListOf()

  public fun execute(): Flow<Event> =
    channelFlow {
      logger.debug { "Started LLM." }
      while (true) {
        val messages = messages + response
        val lastMessage = messages.lastOrNull()
        logger.debug { "Last message: ${lastMessage ?: "null"}." }
        if (with(exitCondition) { shouldExit() }) break
        if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
          executeTools(lastMessage)
        } else {
          chat(buildChatRequest(messages))
        }
      }
      logger.debug { "Ended LLM." }
    }

  @Suppress("SuspendFunWithCoroutineScopeReceiver")
  private suspend fun ProducerScope<Event>.executeTools(lastMessage: AiMessage) {
    val executionRequests = lastMessage.toolExecutionRequests()
    logger.debug { "Tool execution requests: $executionRequests." }
    val executionResults = mutableListOf<ToolExecutionResultMessage>()
    toolExecutor.execute(tools, executionRequests)
      .onEach { event ->
        if (event !is MessageEvent) return@onEach
        if (event.message !is ToolExecutionResultMessage) return@onEach
        if (event.message.id() !in executionRequests.map { it.id() }) return@onEach
        executionResults += event.message
      }
      .onCompletion {
        check(executionResults.size == executionRequests.size)
        logger.debug { "Tool execution responses: $executionResults." }
        response += executionResults
      }
      .collect { send(it) }
  }

  @Suppress("SuspendFunWithCoroutineScopeReceiver")
  private suspend fun ProducerScope<Event>.chat(chatRequest: ChatRequest) {
    logger.debug { "Chat request: $chatRequest." }
    val chatResponse = model.chat(chatRequest)
    logger.debug { "Chat response: $chatResponse." }
    val aiMessage = chatResponse.aiMessage()
    send(MessageEvent(aiMessage))
    response += aiMessage
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
 * Osiris uses Kotlin Flows to provide asynchronous responses with incremental updates.
 * The most basic way to consume the Flow is by calling [response].
 *
 * For more information, refer to the documentation.
 */
@Suppress("LongParameterList")
public fun llm(
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
   * By default, tools are executed in parallel on [Dispatchers.IO] using [ToolExecutor.Dispatcher].
   */
  toolExecutor: ToolExecutor = ToolExecutor.Dispatcher(),
  /**
   * By default, Osiris will run LLM requests in a loop,
   * executing tool calls until the LLM responds.
   */
  exitCondition: ExitCondition = ExitCondition.Default(),
): Flow<Event> {
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
  return llm.execute()
}
