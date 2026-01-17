package osiris

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.ResponseFormat

/**
 * An [Agent] implementation specific to LLM execution.
 *
 * Supports tool calls.
 *
 * [execute] calls the LLM in a loop until a response is provided.
 */
public abstract class LlmAgent(name: String) : Agent(name) {
  public enum class Action {
    CallLlm,
    ExecuteTools,
    Exit,
  }

  context(context: Context)
  final override suspend fun execute() {
    while (true) {
      val action = determineAction()
      when (action) {
        Action.CallLlm -> callLlm()
        Action.ExecuteTools -> throw NotImplementedError()
        Action.Exit -> break
      }
    }
  }

  /**
   * Determines the next [Action] to take in the execution loop.
   * This method can be overridden, but does not normally have to be.
   */
  context(context: Context)
  protected open suspend fun determineAction(): Action {
    val lastMessage = context.history.lastOrNull()
    if (lastMessage is AiMessage) {
      if (lastMessage.hasToolExecutionRequests()) return Action.ExecuteTools
      return Action.Exit
    }
    return Action.CallLlm
  }

  /**
   * Calls LLM, appends response to history.
   * Configure the [ChatRequest] using [configureChatRequest].
   */
  context(context: Context)
  private suspend fun callLlm() {
    val model = model()
    val response = model.chat {
      messages(determineMessages())
      responseFormat()?.let { responseFormat(it) }
      configureChatRequest()
    }
    context.history.append(response.aiMessage())
  }

  /**
   * Override this to specify the [Model].
   * By default, the [Context]'s default model is used.
   */
  context(context: Context)
  protected open fun model(): Model =
    requireNotNull(context.defaultModel) { "No model specified, and default model not set." }

  /**
   * Determines the messages to send to the LLM.
   * This method can be overridden, but does not normally have to be.
   */
  context(context: Context)
  protected open suspend fun determineMessages(): List<ChatMessage> =
    buildList {
      instructions()?.let { add(it) } // Instructions always go first.
      val messages = context.history.get()
      addAll(messages)
      if (messages.isEmpty()) greeting()?.let { add(it) } // Add the greeting if is no history.
    }

  /**
   * The instructions for the agent ([SystemMessage]).
   * The default [determineMessages] implementation always includes this as the first [ChatMessage] in the history.
   */
  context(context: Context)
  protected abstract suspend fun instructions(): SystemMessage?

  /**
   * The (optional) greeting ([UserMessage]).
   * This is only included when there is an empty chat history.
   */
  context(context: Context)
  protected open suspend fun greeting(): UserMessage? = null

  /**
   * Specifies the response format for structured output.
   */
  context(context: Context)
  protected open suspend fun responseFormat(): ResponseFormat? =
    null

  /**
   * Configures the [ChatRequest].
   */
  context(context: Context)
  protected open fun ChatRequest.Builder.configureChatRequest(): Unit =
    Unit
}
