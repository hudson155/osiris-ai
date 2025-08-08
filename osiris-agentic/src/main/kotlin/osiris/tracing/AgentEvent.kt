package osiris.tracing

import osiris.agentic.Agent

internal object AgentEvent {
  fun start(agent: Agent, content: String?): Event.Start.Creator =
    Event.Start.Creator(
      type = "Agent",
      content = content,
      properties = mapOf("agent" to agent),
    )

  fun end(content: String?): Event.End.Creator =
    Event.End.Creator(
      content = content,
    )
}
