package osiris.tracing

/**
 * The outermost span.
 * Both the chat module and the agentic framework will have this as the top level span.
 */
public object TraceEvent {
  public fun start(content: String?): Event.Start.Creator =
    Event.Start.Creator(
      type = "Trace",
      content = content,
    )

  public fun end(content: String?): Event.End.Creator =
    Event.End.Creator(
      content = content,
    )
}
