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
import kairo.coroutines.collect
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import osiris.event.Event
import osiris.event.MessageEvent
import osiris.event.onMessage
import osiris.schema.LlmSchema

private val logger: KLogger = KotlinLogging.logger {}

@Suppress("LongParameterList")
private class Llm(
  val model: ChatModel,
  val messages: List<ChatMessage>,
  val tools: List<Tool<*>>,
  val responseType: KClass<*>?,
  val chatRequestBlock: ChatRequest.Builder.() -> Unit,
  val toolExecutor: ToolExecutor,
  val exitCondition: ExitCondition,
) {
  @Suppress("CognitiveComplexMethod")
  fun execute(): Flow<Event> =
    channelFlow {
      logger.debug { "Started LLM." }
      val response: MutableList<ChatMessage> = mutableListOf()
      while (true) {
        val messages = messages + response
        val lastMessage = messages.lastOrNull()
        logger.debug { "Last message: ${lastMessage ?: "null"}." }
        if (with(exitCondition) { shouldExit(response) }) break
        if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
          executeTools(lastMessage)
            .onMessage { response += it }
            .collect(this)
        } else {
          chat(buildChatRequest(messages))
            .onMessage { response += it }
            .collect(this)
        }
      }
      logger.debug { "Ended LLM." }
    }

  private fun executeTools(lastMessage: AiMessage): Flow<Event> {
    val executionRequests = lastMessage.toolExecutionRequests()
    logger.debug { "Tool execution requests: $executionRequests." }
    val executionResults = mutableListOf<ToolExecutionResultMessage>()
    return toolExecutor.execute(tools, executionRequests)
      .onMessage { message ->
        if (message !is ToolExecutionResultMessage) return@onMessage
        if (message.id() !in executionRequests.map { it.id() }) return@onMessage
        executionResults += message
      }
      .onCompletion {
        logger.debug { "Tool execution results: $executionResults." }
        check(executionResults.size == executionRequests.size)
      }
  }

  private fun chat(chatRequest: ChatRequest): Flow<Event> =
    flow {
      logger.debug { "Chat request: $chatRequest." }
      val chatResponse = model.chat(chatRequest)
      logger.debug { "Chat response: $chatResponse." }
      val aiMessage = chatResponse.aiMessage()
      emit(MessageEvent(aiMessage))
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
