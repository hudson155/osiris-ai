package osiris.osiris

import com.openai.client.OpenAIClientAsync
import com.openai.core.RequestOptions
import com.openai.models.ChatCompletion
import com.openai.models.ChatCompletion.Choice.FinishReason
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessageParam
import kotlinx.coroutines.future.await

internal class OsirisExecution<out Response : Any>(
  private val openAi: OpenAIClientAsync,
  messages: List<ChatCompletionMessageParam>,
  private val options: OsirisOptions<Response>,
) {
  var state = OsirisState.create(messages)

  suspend fun OsirisScope<Response>.execute() {
    withRetries(options.sequentialTries) { i ->
      while (!options.exit(state)) {
        val choices = makeRequest().choices()
        withRetries(choices.size, onError = { send(OsirisEvent.Exception(it)) }) { i ->
          processChoice(choices[i])
        }
      }
    }
    sendResult()
  }

  private suspend fun OsirisScope<Response>.makeRequest(): ChatCompletion {
    val params = ChatCompletionCreateParams.Companion.builder().apply {
      messages(state.messages)
      model(options.model(state))
      options.maxCompletionTokens(state)?.let { maxCompletionTokens(it) }
      n(options.parallelTries(state).toLong())
      parallelToolCalls(options.parallelToolCalls(state))
      options.reasoningEffort(state)?.let { reasoningEffort(it) }
      responseFormat(options.responseType.responseFormat())
      serviceTier(options.serviceTier(state))
      options.temperature(state)?.let { temperature(it) }
      options.toolChoice(state)?.let { toolChoice(it) }
      options.tools(state)?.let { tools(it) }
      options.topP(state)?.let { topP(it) }
      options.user(state)?.let { user(it) }
    }.build()
    val options = RequestOptions.Companion.builder().apply {
      responseValidation(true)
      timeout(options.timeout(state))
    }.build()
    send(OsirisEvent.ChatCompletionRequest(params, options))
    val chatCompletion = openAi.chat().completions().create(params, options).await()
    send(OsirisEvent.ChatCompletionResponse(chatCompletion))
    return chatCompletion
  }

  private fun processChoice(choice: ChatCompletion.Choice) {
    val finishReason = choice.finishReason()
    val message = choice.message()
    var newState = state.appendMessage(ChatCompletionMessageParam.ofAssistant(message.toParam()))
    when(finishReason) {
      FinishReason.Companion.STOP -> {
        state = newState
        return
      }
      FinishReason.Companion.TOOL_CALLS -> {
        TODO()
      }
      else -> {
        error("Unsupported finish reason: $finishReason.")
      }
    }
  }

  private suspend fun OsirisScope<Response>.sendResult() {
    val assistantMessage = state.messages.last { it.isAssistant() }.asAssistant()
    val result = options.responseType.convert(assistantMessage.content().get().asText())
    send(OsirisEvent.Result(result))
  }
}

/**
 * Helper to manage retries.
 *
 * There are 2 types of retries, sequential retries and parallel retries.
 *
 * Sequential retries are handled by [HighbeamAssistant.execute].
 * If any exception gets thrown at all, it will be retried [HighbeamAssistantSettings.tries] times.
 *
 * Parallel retries are handled by [HighbeamAssistant.executeOnState].
 * It's not actually parallel requests; we request multiple response choices back from OpenAI in a single request,
 * then we process them one at a time until one works.
 *
 * [n] must be greater than 0.
 */
private suspend fun withRetries(
  n: Int,
  onError: suspend (e: Throwable) -> Unit = {},
  block: suspend (i: Int) -> Unit,
) {
  check(n > 0) { "Number of retries must be greater than 0." }
  val exceptions = mutableListOf<Throwable>()
  repeat(n) { i ->
    try {
      return block(i)
    } catch (e: Throwable) {
      onError(e)
      exceptions += e
    }
  }
  val e = exceptions.removeLast()
  exceptions.forEach { e.addSuppressed(it) }
  throw e
}
