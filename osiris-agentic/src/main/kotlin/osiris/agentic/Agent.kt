package osiris.agentic

import dev.langchain4j.model.chat.ChatModel

public class Agent(
  public val name: String,
  public val model: ChatModel,
  public val instructions: String?,
)

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
