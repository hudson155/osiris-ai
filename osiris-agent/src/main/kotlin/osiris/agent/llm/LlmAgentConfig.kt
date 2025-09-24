package osiris.agent.llm

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import osiris.Model
import osiris.agent.Context

public interface LlmAgentConfig<C> {
  public suspend fun model(context: C): Model

  public fun instructions(context: C): SystemMessage

  public suspend fun ChatRequest.Builder.llm(context: Context): Unit =
    Unit

  public fun greeting(context: C): UserMessage? =
    null
}
