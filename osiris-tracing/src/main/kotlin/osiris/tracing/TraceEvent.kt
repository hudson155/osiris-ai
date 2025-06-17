package osiris.tracing

/**
 * The outermost span.
 * Both the core module and the agentic framework will have this as the top level span.
 */
public object TraceEvent {
  public data class Start(
    val name: String,
    val input: String?,
  ) : Event.Details()

  public data class End(
    val output: String?,
  ) : Event.Details()
}
