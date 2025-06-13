package osiris.core

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
import osiris.schema.LlmSchema
import osiris.tracing.ChatEvent
import osiris.tracing.trace

private val logger: KLogger = KotlinLogging.logger {}

@Suppress("LongParameterList")
public suspend fun llm(
  model: ChatModel,
  messages: List<ChatMessage>,
  tools: List<Tool<*>> = emptyList(),
  responseType: KClass<*>? = null,
  toolExecutor: ToolExecutor = ToolExecutor.Dispatcher(),
  block: ChatRequest.Builder.() -> Unit = {},
): List<ChatMessage> {
  logger.debug { "Started LLM." }
  val response = mutableListOf<ChatMessage>()
  while (response.lastOrNull()?.let { it is AiMessage && !it.hasToolExecutionRequests() } != true) {
    val chatRequest = buildChatRequest(
      messages = messages + response,
      tools = tools,
      responseType = responseType,
      block = block,
    )
    val lastMessage = chatRequest.messages().lastOrNull()
    logger.debug { "Last message: ${lastMessage ?: "null"}." }
    if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
      val executionRequests = lastMessage.toolExecutionRequests()
      logger.debug { "Tool execution requests: $executionRequests." }
      val executionResponses = toolExecutor.execute(tools, executionRequests)
      logger.debug { "Tool execution responses: $executionResponses." }
      response += executionResponses
    } else {
      logger.debug { "Chat request: $chatRequest." }
      val chatResponse = trace({ ChatEvent(chatRequest, it) }) {
        model.chat(chatRequest)
      }
      logger.debug { "Chat response: $chatResponse." }
      val aiMessage = chatResponse.aiMessage()
      response += aiMessage
    }
  }
  logger.debug { "Ended LLM." }
  return response
}

@Suppress("LongParameterList")
private suspend fun buildChatRequest(
  messages: List<ChatMessage>,
  tools: List<Tool<*>>,
  responseType: KClass<*>?,
  block: ChatRequest.Builder.() -> Unit,
): ChatRequest =
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
    block()
  }.build()
