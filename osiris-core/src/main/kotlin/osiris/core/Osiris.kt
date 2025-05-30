package osiris.core

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

public class Osiris(
  private val model: ChatModel,
  messages: List<ChatMessage>,
  private val tools: Map<String, OsirisTool<*, *>>,
  private val block: ChatRequest.Builder.() -> Unit = {},
) {
  private val messages: MutableList<ChatMessage> = messages.toMutableList()

  public fun execute(): Flow<OsirisEvent> {
    return channelFlow {
      val request = buildRequest()
      val response = makeRequest(request)
      val aiMessage = response.aiMessage()
      addMessage(aiMessage)
      if (aiMessage.hasToolExecutionRequests()) {
        executeTools(aiMessage.toolExecutionRequests())
      }
    }
  }

  private fun buildRequest(): ChatRequest =
    ChatRequest.builder().apply {
      messages(messages)
      if (tools.isNotEmpty()) {
        toolSpecifications(tools.map { it.value.toolSpecification })
      }
      block()
    }.build()

  private fun makeRequest(request: ChatRequest): ChatResponse =
    model.chat(request)

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
  block: ChatRequest.Builder.() -> Unit = {},
): Flow<OsirisEvent> {
  val osiris = Osiris(
    model = model,
    messages = messages,
    tools = tools,
    block = block,
  )
  return osiris.execute()
}
