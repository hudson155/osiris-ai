package osiris.agentic

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import osiris.core.llm

public class Agent(
  public val name: String,
  public val model: ChatModel,
  public val instructions: String?,
) {
  public suspend fun run(messages: List<ChatMessage>): AiMessage =
    llm(
      model = model,
      messages = messages + SystemMessage(instructions),
    )
}

public class AgentBuilder internal constructor(
  private val name: String,
) {
  public var model: ChatModel? = null
  public var instructions: String? = null

  internal fun build(): Agent {
    val model = requireNotNull(model) { "Agent $name must set a model." }
    return Agent(
      name = name,
      model = model,
      instructions = instructions,
    )
  }
}

public fun agent(name: String, block: AgentBuilder.() -> Unit): Agent =
  AgentBuilder(name).apply(block).build()
