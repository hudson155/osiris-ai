package osiris.osiris

import com.openai.models.ChatCompletionMessageParam

public data class OsirisState private constructor(
  val messages: List<ChatCompletionMessageParam>,
  val newMessages: Int,
) {
  public fun appendMessage(message: ChatCompletionMessageParam): OsirisState =
    OsirisState(
      messages = messages + message,
      newMessages = newMessages + 1,
    )

  public companion object {
    internal fun create(messages: List<ChatCompletionMessageParam>): OsirisState =
      OsirisState(
        messages = messages,
        newMessages = 0,
      )
  }
}
