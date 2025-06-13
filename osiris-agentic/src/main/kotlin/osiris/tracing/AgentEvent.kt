package osiris.tracing

import osiris.agentic.Agent

public data class AgentEvent(
  val agent: Agent,
  val input: String?,
  val output: String?,
) : Span.Details()
