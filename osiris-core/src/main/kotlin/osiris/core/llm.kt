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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import osiris.schema.llmSchema
import osiris.schema.osirisSchemaName

private val logger: KLogger = KotlinLogging.logger {}

@Suppress("LongParameterList")
public fun llm(
  model: ChatModel,
  messages: List<ChatMessage>,
  tools: List<Tool<*, *>> = emptyList(),
  responseType: KClass<*>? = null,
  toolExecutor: ToolExecutor = ToolExecutor.Default(),
  block: ChatRequest.Builder.() -> Unit = {},
): Flow<ChatMessage> {
  @Suppress("NoNameShadowing")
  val messages = messages.toMutableList()
  return flow {
    while (true) {
      logger.debug { "Messages: $messages." }
      val chatRequest = buildChatRequest(
        messages = messages,
        tools = tools,
        responseType = responseType,
        block = block,
      )
      val lastMessage = chatRequest.messages().lastOrNull()
      if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
        val executionRequests = lastMessage.toolExecutionRequests()
        logger.debug { "Executing tools: $executionRequests." }
        val executionResponses = toolExecutor.execute(tools, executionRequests)
        logger.debug { "Executed tools: $executionResponses." }
        emitAll(executionResponses.asFlow())
        messages += executionResponses
      } else {
        logger.debug { "Chat request: $chatRequest." }
        val chatResponse = model.chat(chatRequest)
        logger.debug { "Chat response: $chatResponse." }
        val aiMessage = chatResponse.aiMessage()
        emit(aiMessage)
        messages += aiMessage
      }
    }
  }
}

@Suppress("LongParameterList")
private fun buildChatRequest(
  messages: List<ChatMessage>,
  tools: List<Tool<*, *>>,
  responseType: KClass<*>?,
  block: ChatRequest.Builder.() -> Unit,
): ChatRequest =
  ChatRequest.builder().apply {
    messages(messages)
    if (tools.isNotEmpty()) {
      toolSpecifications(tools.map { it.toolSpecification })
    }
    if (responseType != null) {
      val jsonSchema = JsonSchema.builder().apply {
        name(osirisSchemaName(responseType))
        rootElement(llmSchema(responseType))
      }.build()
      val responseFormat = ResponseFormat.builder().apply {
        type(ResponseFormatType.JSON)
        jsonSchema(jsonSchema)
      }.build()
      responseFormat(responseFormat)
    }
    block()
  }.build()
