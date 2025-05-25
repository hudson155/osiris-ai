package osiris.ennead

public class Agent<State> internal constructor(
  public val name: String,
  private val implementation: AgentImplementation<State>,
) {
  internal suspend fun execute(context: AgentContext<State>): AgentContext<State> =
    implementation.execute(context)
}

public class AgentBuilder<State> internal constructor(
  private val name: String,
) {
  public var description: String? = null

  public var implementation: AgentImplementation<State>? = null
    set(value) {
      require(field == null) { "Implementation has already been set." }
      field = value
    }

  internal fun build(): Agent<State> {
    val implementation = requireNotNull(implementation) { "Implementation must be provided." }
    return Agent(
      name = name,
      implementation = implementation,
    )
  }
}

public fun <State> agent(name: String, block: AgentBuilder<State>.() -> Unit): Agent<State> =
  AgentBuilder<State>(name).apply(block).build()
