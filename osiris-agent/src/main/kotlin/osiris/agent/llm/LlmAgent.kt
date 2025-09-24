package osiris.agent.llm

import dev.langchain4j.data.message.ChatMessage
import osiris.agent.Agent
import osiris.agent.Context

public abstract class LlmAgent<C>(
  name: String,
) : Agent<C>(name), LlmAgentConfig<C> where C : Context, C : LlmContext {
  final override suspend fun run(context: C) {
    val history = context.history.get()
    val action = LlmAction.fromHistory(history)
    when (action) {
      LlmAction.Greet -> greet(context)
      LlmAction.Llm -> llm(context)
    }
  }

  private suspend fun greet(context: C) {
    val messages = listOf(instructions(context), greeting(context) ?: return)
    chat(context, messages)
  }

  private suspend fun llm(context: C) {
    val messages = buildList {
      add(instructions(context))
      addAll(context.history.get())
    }
    chat(context, messages)
  }

  private suspend fun chat(context: C, messages: List<ChatMessage>) {
    val model = model(context)
    val aiResponse = model.chat {
      messages(messages)
      llm(context)
    }
    context.history.append(aiResponse.aiMessage())
  }
}
