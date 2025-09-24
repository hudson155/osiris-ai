package osiris.agent

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import osiris.Model

public abstract class LlmAgent<C>(
  name: String,
) : Agent<C>(name) where C : Context, C : LlmContext {
  override suspend fun execute(context: C) {
    val history = context.history.get()
    val messages = buildList {
      add(instructions(context))
      addAll(history)
      if (history.isEmpty()) add(greeting(context) ?: return)
    }
    val model = model(context)
    val aiResponse = model.chat {
      messages(messages)
      llm(context)
    }
    context.history.append(aiResponse.aiMessage())
  }

  protected abstract suspend fun model(context: C): Model

  protected abstract fun instructions(context: C): SystemMessage

  protected open suspend fun ChatRequest.Builder.llm(context: Context): Unit =
    Unit

  protected open fun greeting(context: C): UserMessage? =
    null
}
