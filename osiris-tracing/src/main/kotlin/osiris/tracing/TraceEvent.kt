package osiris.tracing

public object TraceEvent {
  public data class Start(
    val name: String,
    val input: String?,
  ) : Event.Details()

  public data class End(
    val output: String?,
  ) : Event.Details()
}
