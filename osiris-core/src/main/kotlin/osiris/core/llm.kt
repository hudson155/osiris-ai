package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonSchema
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import osiris.event.ChatEvent
import osiris.event.Event
import osiris.event.MessageEvent
import osiris.schema.llmSchema
import osiris.schema.llmSchemaName

@Suppress("LongParameterList")
public fun llm(
  model: ChatModel,
  messages: List<ChatMessage>,
  tools: List<Tool<*, *>> = emptyList(),
  responseType: KClass<*>? = null,
  toolExecutor: ToolExecutor = ToolExecutor.Default(),
  block: ChatRequest.Builder.() -> Unit = {},
): Flow<Event> {
  @Suppress("NoNameShadowing")
  val messages = messages.toMutableList()
  return channelFlow {
    withContext(LlmContext(this)) {
      while (true) {
        val chatRequest = buildChatRequest(
          messages = messages,
          tools = tools,
          responseType = responseType,
          block = block,
        )
        val lastMessage = chatRequest.messages().lastOrNull()
        if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
          val executionRequests = lastMessage.toolExecutionRequests()
          val executionResponses = toolExecutor.execute(tools, executionRequests)
          messages += executionResponses
          executionResponses.forEach { send(MessageEvent(it)) }
        } else {
          send(ChatEvent.Start(request = chatRequest))
          val chatResponse = model.chat(chatRequest)
          send(ChatEvent.End(request = chatRequest, response = chatResponse))
          val aiMessage = chatResponse.aiMessage()
          messages += aiMessage
          send(MessageEvent(aiMessage))
        }
        yield()
      }
    }
  }
}

@Suppress("LongParameterList")
private suspend fun buildChatRequest(
  messages: List<ChatMessage>,
  tools: List<Tool<*, *>>,
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
        name(llmSchemaName(responseType))
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
