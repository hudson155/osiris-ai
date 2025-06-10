package osiris.span

import osiris.core.Tool

public data class ToolEvent<Input : Any, out Output : Any>(
  val tool: Tool<Input, Output>,
  val id: String,
  val input: Input,
  val output: Output,
) : Span.Details()
