package osiris.event

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.data.message.UserMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public sealed class MessageEvent : Event() {
  public abstract fun toMessage(): ChatMessage

  @Serializable
  @SerialName("AiMessage")
  public data class Ai(
    val agentName: String,
    val text: String?,
    val thinking: String?,
    val tools: List<Tool>,
  ) : MessageEvent() {
    @Serializable
    public data class Tool(
      val id: String,
      val name: String,
      val arguments: String,
    )

    override fun toMessage(): AiMessage =
      AiMessage.builder().apply {
        text?.let { text(it) }
        thinking?.let { thinking(it) }
        if (tools.isNotEmpty()) {
          toolExecutionRequests(
            tools.map { tool ->
              ToolExecutionRequest.builder().apply {
                id(tool.id)
                name(tool.name)
                arguments(tool.arguments)
              }.build()
            },
          )
        }
      }.build()

    public companion object {
      public fun from(agentName: String, message: AiMessage): Ai =
        Ai(
          agentName = agentName,
          text = message.text(),
          thinking = message.thinking(),
          tools = message.toolExecutionRequests().orEmpty().map { tool ->
            Tool(
              id = tool.id(),
              name = tool.name(),
              arguments = tool.arguments(),
            )
          },
        )
    }
  }

  @Serializable
  @SerialName("UserMessage")
  public data class User(
    val contents: List<Content>,
  ) : MessageEvent() {
    @Serializable
    public sealed class Content {
      public abstract fun toContent(): dev.langchain4j.data.message.Content

      @Serializable
      @SerialName("Text")
      public data class Text(val text: String) : Content() {
        override fun toContent(): TextContent =
          TextContent.from(text)
      }
    }

    override fun toMessage(): UserMessage =
      UserMessage.from(contents.map { it.toContent() })

    public companion object {
      public fun from(message: UserMessage): User =
        User(
          message.contents().map { content ->
            when (content) {
              is TextContent -> Content.Text(content.text())
              else -> error("Unsupported content type (contentType=${content::class}).")
            }
          },
        )

      public fun from(text: String): User =
        User(listOf(Content.Text(text)))
    }
  }
}
