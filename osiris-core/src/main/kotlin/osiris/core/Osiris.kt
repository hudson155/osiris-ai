package osiris.core

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

public fun osiris(
  model: ChatModel,
  messages: List<ChatMessage>,
  tools: Map<String, OsirisTool<*, *>>,
  buildRequest: ChatRequest.Builder.() -> Unit = {},
): Flow<OsirisEvent> =
  channelFlow {
    val request = ChatRequest.builder().apply {
      messages(messages)
      if (tools.isNotEmpty()) {
        toolSpecifications(tools.map { it.value.toolSpecification })
      }
      buildRequest()
    }.build()
    val response = model.chat(request)
    val aiMessage = response.aiMessage()
    send(OsirisEvent.Message(aiMessage))
    if (aiMessage.hasToolExecutionRequests()) {
      // TODO: Parallelize tool calls.
      aiMessage.toolExecutionRequests().forEach { execution ->
        val toolName = execution.name()
        val tool = checkNotNull(tools[toolName]) { "No tool with name: $toolName." }
        val output = tool(execution.arguments())
        val executionMessage = ToolExecutionResultMessage(execution.id(), toolName, output)
        send(OsirisEvent.Message(executionMessage))
      }
    }
  }
