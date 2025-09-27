package osiris.agent.llm

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import osiris.Model
import osiris.agent.Context

public interface LlmAgentConfig {
  public suspend fun model(context: Context): Model

  public fun instructions(context: Context): SystemMessage

  public fun tools(context: Context): List<Tool<*, *>> =
    emptyList()

  public suspend fun ChatRequest.Builder.llm(context: Context): Unit =
    Unit

  public fun greeting(context: Context): UserMessage? =
    null
}
