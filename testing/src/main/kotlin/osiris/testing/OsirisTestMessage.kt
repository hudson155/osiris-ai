package osiris.testing

import dev.langchain4j.model.chat.request.ChatRequest

internal data class OsirisTestMessage<out Response : Any>(
  val name: String,
  val request: ChatRequest,
  val evals: List<OsirisEval<Response>>,
)
