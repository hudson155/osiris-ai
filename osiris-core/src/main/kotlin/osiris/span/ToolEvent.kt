package osiris.span

import osiris.core.Tool

public data class ToolEvent(
  val tool: Tool<*, *>,
  val id: String,
  val input: Any,
  val output: Any,
) : Span.Details()
