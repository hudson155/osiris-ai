package osiris.event

import java.time.Instant
import osiris.agentic.Agent

public sealed class AgentEvent : Event() {
  public data class Start(
    val at: Instant,
    val agent: Agent,
  ) : AgentEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(agent: Agent) : this(
      at = Instant.now(),
      agent = agent,
    )
  }

  public data class End(
    val at: Instant,
  ) : AgentEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor() : this(
      at = Instant.now(),
    )
  }
}
