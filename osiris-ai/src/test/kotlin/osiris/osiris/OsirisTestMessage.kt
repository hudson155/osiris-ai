package osiris.osiris

internal data class OsirisTestMessage<out Response : Any>(
  val request: String,
  val evals: List<OsirisEval<Response>>,
)
