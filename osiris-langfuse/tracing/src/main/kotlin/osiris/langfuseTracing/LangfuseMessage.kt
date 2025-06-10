package osiris.langfuseTracing

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.data.message.UserMessage
import osiris.event.ChatEvent

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "role")
@JsonSubTypes(
  JsonSubTypes.Type(LangfuseMessage.Assistant::class, "assistant"),
  JsonSubTypes.Type(LangfuseMessage.Tool::class, "tool"),
  JsonSubTypes.Type(LangfuseMessage.System::class, "system"),
  JsonSubTypes.Type(LangfuseMessage.User::class, "user"),
)
internal sealed class LangfuseMessage {
  internal data class Assistant(
    val content: String?,
    val toolCalls: List<ToolCall>?,
  ) : LangfuseMessage() {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(
      JsonSubTypes.Type(ToolCall.Function::class, "function"),
    )
    internal sealed class ToolCall {
      abstract val id: String

      internal data class Function(
        override val id: String,
        val function: Function,
      ) : ToolCall() {
        internal data class Function(
          val name: String,
          val arguments: String,
        )
      }
    }
  }

  internal data class Tool(
    val id: String,
    val name: String,
    val content: String,
  ) : LangfuseMessage()

  internal data class System(
    val content: String,
  ) : LangfuseMessage()

  internal data class User(
    val content: String,
  ) : LangfuseMessage()

  internal companion object {
    @Suppress("LongMethod")
    fun extract(start: ChatEvent.Start, end: ChatEvent.End): List<LangfuseMessage> {
      val messages = start.request.messages() + end.response.aiMessage()
      return messages.mapNotNull { message ->
        when (message) {
          is AiMessage -> run {
            val content = message.text()
            val toolCalls = run {
              if (!message.hasToolExecutionRequests()) return@run null
              return@run message.toolExecutionRequests()
                .map { executionRequest ->
                  Assistant.ToolCall.Function(
                    id = executionRequest.id(),
                    function = Assistant.ToolCall.Function.Function(
                      name = executionRequest.name(),
                      arguments = executionRequest.arguments(),
                    ),
                  )
                }
            }
            return@run Assistant(
              content = content,
              toolCalls = toolCalls,
            )
          }
          is ToolExecutionResultMessage -> run {
            Tool(
              id = message.id(),
              name = message.toolName(),
              content = message.text(),
            )
          }
          is SystemMessage -> run {
            System(
              content = message.text(),
            )
          }
          is UserMessage -> run {
            User(
              content = message.singleText(),
            )
          }
          else -> null
        }
      }
    }
  }
}
