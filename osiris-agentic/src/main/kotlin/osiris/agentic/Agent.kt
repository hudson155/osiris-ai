package osiris.agentic

public class Agent(
  public val name: String,
)

public class AgentBuilder internal constructor(
  private val name: String,
) {
  internal fun build(): Agent =
    Agent(
      name = name,
    )
}

public fun agent(name: String, block: AgentBuilder.() -> Unit): Agent =
  AgentBuilder(name).apply(block).build()
