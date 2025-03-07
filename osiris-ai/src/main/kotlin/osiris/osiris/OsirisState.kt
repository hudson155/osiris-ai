package osiris.osiris

import com.openai.models.ChatCompletionMessageParam

public data class OsirisState internal constructor(
  val messages: List<ChatCompletionMessageParam>,
  val newMessages: Int,
  val sequentialTry: Int,
)
