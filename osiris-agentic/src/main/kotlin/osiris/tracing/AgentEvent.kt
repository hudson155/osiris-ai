package osiris.tracing

import osiris.agentic.Agent

/**
 * Each agent's turn will have a span.
 */
public object AgentEvent {
  public data class Start(
    val agent: Agent,
    val input: String?,
  ) : Event.Details()

  public data class End(
    val output: String?,
  ) : Event.Details()
}
