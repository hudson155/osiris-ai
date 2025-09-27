package osiris.agent.llm

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import osiris.agent.Agent
import osiris.agent.Context

public suspend fun Agent.run(userMessage: UserMessage): String {
  val context = Context().apply { history.append(userMessage) }
  run(context)
  val aiMessage = context.history.get().last() as AiMessage
  return aiMessage.text()
}
