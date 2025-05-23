package osiris.ennead

public class AgentRunner<State> internal constructor(
  private val agents: Map<String, Agent<State>>,
) {
  public fun run(initialState: State, initialAgentName: String): State {
    val initialAgent = requireNotNull(agents[initialAgentName]) { "No agent with name: $initialAgentName." }
    return initialAgent.execute(initialState).state
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
