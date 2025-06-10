package osiris.event

import java.time.Instant

public sealed class ExecutionEvent : Event() {
  public data class Start(
    val at: Instant,
    val name: String,
  ) : ExecutionEvent() {
    public constructor(name: String) : this(
      at = Instant.now(),
      name = name,
    )
  }

  public data class End(
    val at: Instant,
    val name: String,
  ) : ExecutionEvent() {
    public constructor(name: String) : this(
      at = Instant.now(),
      name = name,
    )
  }
}
