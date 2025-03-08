package osiris.osiris

import com.openai.core.RequestOptions
import com.openai.models.ChatCompletion
import com.openai.models.ChatCompletionCreateParams

/**
 * Events are emitted asynchronously during execution.
 * You can listen to all events, but the only
 */
public sealed class OsirisEvent<out Response : Any> {
  public data class ChatCompletionRequest internal constructor(
    val params: ChatCompletionCreateParams,
    val options: RequestOptions,
  ) : OsirisEvent<Nothing>()

  public data class ChatCompletionResponse internal constructor(
    val chatCompletion: ChatCompletion,
  ) : OsirisEvent<Nothing>()

  public data class Exception internal constructor(
    val e: Throwable,
  ) : OsirisEvent<Nothing>()

  public data class Result<out Result : Any> internal constructor(
    val result: Result,
  ) : OsirisEvent<Result>()
}
