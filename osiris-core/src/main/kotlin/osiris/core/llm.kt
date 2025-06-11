package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonSchema
import kotlin.reflect.KClass
import kotlinx.coroutines.withContext
import osiris.schema.llmSchema
import osiris.schema.llmSchemaName
import osiris.span.ChatEvent
import osiris.span.Span

@Suppress("LongParameterList")
public suspend fun llm(
  model: ChatModel,
  messages: List<ChatMessage>,
  tools: List<Tool<*, *>> = emptyList(),
  responseType: KClass<*>? = null,
  toolExecutor: ToolExecutor = ToolExecutor.Dispatcher(),
  block: ChatRequest.Builder.() -> Unit = {},
): Pair<List<ChatMessage>, List<Span<*>>> {
  val response = mutableListOf<ChatMessage>()
  val traceContext = TraceContext.create()
  withContext(traceContext) {
    while (response.lastOrNull()?.let { it is AiMessage && !it.hasToolExecutionRequests() } != true) {
      val chatRequest = buildChatRequest(
        messages = messages + response,
        tools = tools,
        responseType = responseType,
        block = block,
      )
      val lastMessage = chatRequest.messages().lastOrNull()
      if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
        val executionRequests = lastMessage.toolExecutionRequests()
        val executionResponses = toolExecutor.execute(tools, executionRequests)
        response += executionResponses
      } else {
        val chatResponse = trace({ ChatEvent(chatRequest, it) }) {
          model.chat(chatRequest)
        }
        val aiMessage = chatResponse.aiMessage()
        response += aiMessage
      }
    }
  }
  return Pair(response, traceContext.spans)
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
