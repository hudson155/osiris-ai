package osiris.tracing

import osiris.agentic.Network

public data class ExecutionEvent(
  val network: Network,
  val input: String?,
  val output: String?,
) : Span.Details()
