package osiris.event

import java.time.Instant
import osiris.agentic.Agent

public sealed class AgentEvent : Event() {
  public data class Start(
    override val at: Instant,
    val agent: Agent,
    val input: String?,
  ) : AgentEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(
      agent: Agent,
      input: String?,
    ) : this(
      at = Instant.now(),
      agent = agent,
      input = input,
    )
  }

  public data class End(
    override val at: Instant,
    val output: String?,
  ) : AgentEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(output: String?) : this(
      at = Instant.now(),
      output = output,
    )
  }
}
