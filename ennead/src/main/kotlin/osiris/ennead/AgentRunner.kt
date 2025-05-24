package osiris.ennead

public class AgentRunner<State> internal constructor(
  private val agents: Map<String, Agent<State>>,
) {
  public fun run(initialState: State, initialAgentName: String): State {
    var context = AgentContext(
      state = initialState,
      currentAgentName = initialAgentName,
      nextAgentNames = listOf(initialAgentName),
    )
    while (context.nextAgentNames.isNotEmpty()) {
      val currentAgentName = context.nextAgentNames.first()
      context = context.copy(currentAgentName = currentAgentName, nextAgentNames = context.nextAgentNames.drop(1))
      val agent = requireNotNull(agents[currentAgentName]) { "No agent with name: $currentAgentName." }
      context = agent.execute(context)
    }
    return context.state
  }
}

public class AgentRunnerBuilder<State> internal constructor() {
  private val agents: MutableMap<String, Agent<State>> = mutableMapOf()

  public fun agent(agent: Agent<State>) {
    require(agents[agent.name] == null) { "Duplicate agent with name: ${agent.name}." }
    agents[agent.name] = agent
  }

  internal fun build(): AgentRunner<State> =
    AgentRunner(agents)
}

public fun <State> runner(block: AgentRunnerBuilder<State>.() -> Unit): AgentRunner<State> =
  AgentRunnerBuilder<State>().apply(block).build()
