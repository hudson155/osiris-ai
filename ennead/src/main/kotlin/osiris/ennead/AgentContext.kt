package osiris.ennead

public data class AgentContext<State>(
  val state: State,
  val currentAgentName: String,
  val nextAgentNames: List<String>,
)
