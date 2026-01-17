package osiris

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.ResponseFormat

public abstract class LlmAgent(name: String) : Agent(name) {
  public enum class Action {
    CallLlm,
    ExecuteTools,
    Exit
  }

  context(context: Context)
  final override suspend fun execute() {
    while (true) {
      val action = determineAction()
      when (action) {
        Action.CallLlm -> callLlm()
        Action.ExecuteTools -> TODO()
        Action.Exit -> break
      }
    }
  }

  context(context: Context)
  protected open suspend fun determineAction(): Action {
    val lastMessage = context.history.get().last()
    if (lastMessage is AiMessage) {
      if (lastMessage.hasToolExecutionRequests()) return Action.ExecuteTools
      return Action.Exit
    }
    return Action.CallLlm
  }

  context(context: Context)
  private suspend fun callLlm() {
    val model = determineModel()
    val response = model.chat {
      messages(determineMessages())
      determineResponseFormat()?.let { responseFormat(it) }
      configureChatRequest()
    }
    context.history.append(response.aiMessage())
  }

  context(context: Context)
  protected open fun determineModel(): Model =
    requireNotNull(context.defaultModel) { "No model specified, and default model not set." }

  context(context: Context)
  protected open suspend fun determineMessages(): List<ChatMessage> =
    buildList {
      instructions()?.let { add(it) }
      val messages = context.history.get()
      addAll(messages)
      if (messages.isEmpty()) greeting()?.let { add(it) }
    }

  context(context: Context)
  protected abstract suspend fun instructions(): SystemMessage?

  context(context: Context)
  protected open suspend fun greeting(): UserMessage? = null

  context(context: Context)
  protected open suspend fun determineResponseFormat(): ResponseFormat? =
    null

  context(context: Context)
  protected open fun ChatRequest.Builder.configureChatRequest(): Unit =
    Unit
}
