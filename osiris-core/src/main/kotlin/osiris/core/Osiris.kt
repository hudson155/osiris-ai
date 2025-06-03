package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonSchema
import dev.langchain4j.model.chat.response.ChatResponse
import kotlin.reflect.KClass
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import osiris.schema.osirisSchema
import osiris.schema.osirisSchemaName

@Suppress("LongParameterList", "SuspendFunWithCoroutineScopeReceiver")
public class Osiris(
  private val model: ChatModel,
  messages: List<ChatMessage>,
  private val tools: Map<String, OsirisTool<*, *>>,
  private val responseType: KClass<*>?,
  private val block: ChatRequest.Builder.() -> Unit,
) {
  private val messages: MutableList<ChatMessage> = messages.toMutableList()

  public fun execute(executeTools: Boolean): Flow<OsirisEvent> =
    channelFlow {
      do {
        val chatRequest = buildChatRequest()
        val chatResponse = makeChatRequest(chatRequest)
        val aiMessage = chatResponse.aiMessage()
        addMessage(aiMessage)
        val hasToolExecutionRequests = aiMessage.hasToolExecutionRequests() && executeTools
        if (hasToolExecutionRequests) {
          executeTools(aiMessage.toolExecutionRequests())
        }
      } while (hasToolExecutionRequests)
    }

  private fun buildChatRequest(): ChatRequest =
    ChatRequest.builder().apply {
      messages(messages)
      if (tools.isNotEmpty()) {
        toolSpecifications(tools.map { it.value.toolSpecification })
      }
      if (responseType != null) {
        val jsonSchema = JsonSchema.builder()
          .name(osirisSchemaName(responseType))
          .rootElement(osirisSchema(responseType))
          .build()
        val responseFormat = ResponseFormat.builder()
          .type(ResponseFormatType.JSON)
          .jsonSchema(jsonSchema)
          .build()
        responseFormat(responseFormat)
      }
      block()
    }.build()

  private fun makeChatRequest(chatRequest: ChatRequest): ChatResponse =
    model.chat(chatRequest)

  private suspend fun ProducerScope<OsirisEvent>.addMessage(message: ChatMessage) {
    messages += message
    send(OsirisEvent.Message(message))
  }

  private suspend fun ProducerScope<OsirisEvent>.executeTools(executions: List<ToolExecutionRequest>) {
    // TODO: Parallelize tool calls.
    executions.forEach { execution ->
      executeTool(execution)
    }
  }

  private suspend fun ProducerScope<OsirisEvent>.executeTool(execution: ToolExecutionRequest) {
    val toolName = execution.name()
    val tool = checkNotNull(tools[toolName]) { "No tool with name: $toolName." }
    val output = tool(execution.arguments())
    val executionMessage = ToolExecutionResultMessage(execution.id(), toolName, output)
    addMessage(executionMessage)
  }
}

@Suppress("LongParameterList")
public fun osiris(
  model: ChatModel,
  messages: List<ChatMessage>,
  tools: Map<String, OsirisTool<*, *>> = emptyMap(),
  responseType: KClass<*>? = null,
  executeTools: Boolean = true,
  block: ChatRequest.Builder.() -> Unit = {},
): Flow<OsirisEvent> {
  val osiris = Osiris(
    model = model,
    messages = messages,
    tools = tools,
    responseType = responseType,
    block = block,
  )
  return osiris.execute(executeTools = executeTools)
}
