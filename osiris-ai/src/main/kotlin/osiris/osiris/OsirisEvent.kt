package osiris.osiris

import com.openai.core.RequestOptions
import com.openai.models.ChatCompletion
import com.openai.models.ChatCompletionCreateParams

/**
 * Events are emitted asynchronously during execution.
 * The only event that's strictly necessary to listen for is [Result].
 * Other event types are for logging and debugging.
 */
public sealed class OsirisEvent<out Response : Any> {
  /**
   * The raw "chat completion" request being made to OpenAI.
   * This event is sent immediately before the request is made.
   */
  public data class ChatCompletionRequest internal constructor(
    val params: ChatCompletionCreateParams,
    val options: RequestOptions,
  ) : OsirisEvent<Nothing>()

  /**
   * The raw "chat completion" request being made to OpenAI.
   * This event is sent immediately before the request is made.
   */
  public data class ChatCompletionResponse internal constructor(
    val chatCompletion: ChatCompletion,
  ) : OsirisEvent<Nothing>()

  /**
   * Any exceptions that occur during execution.
   * If an exception is terminal it will also be thrown, but many exceptions are masked by retries.
   * All exceptions will be sent as events.
   */
  public data class Exception internal constructor(
    val e: Throwable,
  ) : OsirisEvent<Nothing>()

  /**
   * The final result.
   * Unless an exception is thrown, there will be exactly 1 of these.
   */
  public data class Result<out Result : Any> internal constructor(
    val result: Result,
  ) : OsirisEvent<Result>()
}
