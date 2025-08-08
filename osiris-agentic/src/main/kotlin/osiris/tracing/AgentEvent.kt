package osiris.tracing

/**
 * Each Agent's turn will have a span.
 */
internal object AgentEvent {
  fun start(content: String?): Event.Start.Creator =
    Event.Start.Creator(
      type = "Agent",
      content = content,
    )

  fun end(content: String?): Event.End.Creator =
    Event.End.Creator(
      content = content,
    )
}
