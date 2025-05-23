package osiris.ennead

public data class AgentContext<State>(
  val state: State,
  val nextAgentNames: List<String>,
)
