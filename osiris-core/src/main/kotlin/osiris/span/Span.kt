package osiris.span

import java.time.Instant

public data class Span<T : Span.Details>(
  val start: Instant,
  val end: Instant,
  val details: T,
) {
  public abstract class Details
}
