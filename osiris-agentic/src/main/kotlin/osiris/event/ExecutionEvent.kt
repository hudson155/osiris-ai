package osiris.event

import java.time.Instant
import osiris.agentic.Network

public sealed class ExecutionEvent : Event() {
  public data class Start(
    override val at: Instant,
    val network: Network,
    val input: String?,
  ) : ExecutionEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(
      network: Network,
      input: String?,
    ) : this(
      at = Instant.now(),
      network = network,
      input = input,
    )
  }

  public data class End(
    override val at: Instant,
    val output: String?,
  ) : ExecutionEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(output: String) : this(
      at = Instant.now(),
      output = output,
    )
  }
}
