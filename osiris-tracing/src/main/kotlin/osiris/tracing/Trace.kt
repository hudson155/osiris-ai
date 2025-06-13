package osiris.tracing

public data class Trace(
  val spans: List<Span<*>>,
)
