package osiris.event

import osiris.agentic.Agent

public sealed class AgentEvent : Event() {
  public data class Start(
    val agent: Agent,
  ) : AgentEvent()

  public data class End(
    val agent: Agent,
  ) : AgentEvent()
}
