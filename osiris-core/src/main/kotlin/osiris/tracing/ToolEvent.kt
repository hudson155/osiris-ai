package osiris.tracing

import osiris.core.Tool

public data class ToolEvent(
  val tool: Tool<*>,
  val id: String,
  val input: Any,
  val output: String,
) : Span.Details()
