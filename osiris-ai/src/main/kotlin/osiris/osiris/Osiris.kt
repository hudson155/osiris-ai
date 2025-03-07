package osiris.osiris

import com.openai.client.OpenAIClientAsync
import com.openai.core.RequestOptions
import com.openai.models.ChatCompletion
import com.openai.models.ChatCompletion.Choice.FinishReason
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessage
import com.openai.models.ChatCompletionMessageParam
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.future.await

private val logger: KLogger = KotlinLogging.logger {}

public class Osiris(
  private val openAi: OpenAIClientAsync,
) {
  public fun <Response : Any> execute(
    messages: List<ChatCompletionMessageParam>,
    options: OsirisOptions<Response>,
  ): Flow<OsirisEvent<Response>> {
    logger.info { "Osiris is executing." }
    var state = OsirisState(
      messages = messages,
      newMessages = 0,
      sequentialTry = 0,
    )
    return channelFlow {
      withRetries(options.sequentialTries) sequentialTry@{ sequentialTry ->
        state = state.copy(sequentialTry = sequentialTry)
        while (!options.exit(state)) {
          val choices = makeRequest(state, options).choices()
          return@sequentialTry withRetries(
            n = choices.size,
            onError = { send(OsirisEvent.Exception(it)) },
          ) parallelTry@{ parallelTry ->
            val choice = choices[parallelTry]
            val message = choice.message()
            var newState = state.copy(
              messages = state.messages + ChatCompletionMessageParam.ofAssistant(message.toParam()),
              newMessages = state.newMessages + 1,
            )
            when (val finishReason = choice.finishReason().value()) {
              FinishReason.Value.STOP -> Unit
              FinishReason.Value.TOOL_CALLS ->
                newState = newState.copy(messages = newState.messages + makeToolCalls(message))
              else -> error("Unsupported finish reason: $finishReason.")
            }
            state = newState
          }
        }
      }
    }

    // todo: add explciit BREAK
  }

  private suspend fun ProducerScope<OsirisEvent<Nothing>>.makeRequest(
    state: OsirisState,
    options: OsirisOptions<*>,
  ): ChatCompletion {
    val params = ChatCompletionCreateParams.builder().apply {
      messages(state.messages)
      model(options.model(state))
      options.maxCompletionTokens(state)?.let { maxCompletionTokens(it) }
      n(options.parallelTries(state).toLong())
      parallelToolCalls(options.parallelToolCalls(state))
      options.reasoningEffort(state)?.let { reasoningEffort(it) }
      responseFormat(options.responseType(state).responseFormat())
      serviceTier(options.serviceTier(state))
      options.temperature(state)?.let { temperature(it) }
      options.toolChoice(state)?.let { toolChoice(it) }
      options.tools(state)?.let { tools(it) }
      options.topP(state)?.let { topP(it) }
      options.user(state)?.let { user(it) }
    }.build()
    val options = RequestOptions.builder().apply {
      responseValidation(true)
      timeout(options.timeout(state))
    }.build()
    send(OsirisEvent.ChatCompletionRequest(params, options))
    val chatCompletion = openAi.chat().completions().create(params, options).await()
    send(OsirisEvent.ChatCompletionResponse(chatCompletion))
    return chatCompletion
  }

  private fun makeToolCalls(message: ChatCompletionMessage): ChatCompletionMessageParam {
    // val toolCalls = message.toolCalls().get()
    // val results = toolCalls.associate { toolCall ->
    //   // TODO: Run these in parallel if config says so.
    //   val function = functionProvider!!.get(toolCall.function.name)!!
    //   val arguments = jsonMapper.readValue<JsonNode>(toolCall.function.arguments)
    //   val result = when (val result = function.execute(arguments)) {
    //     is Result.String -> result.string
    //     is Result.Json -> jsonMapper.kairoWrite(result.json)
    //   }
    //   return@associate Pair(toolCall.id, result)
    // }
    TODO()
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
