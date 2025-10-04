package osiris.agent.llm

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.json.JsonSchemaElement
import osiris.Model
import osiris.agent.Context
import osiris.agent.tool.Tool

public interface LlmAgentConfig {
  public suspend fun model(context: Context): Model

  public fun instructions(context: Context): SystemMessage?

  public fun tools(context: Context): List<Tool<*, *>> =
    emptyList()

  public fun schema(context: Context): JsonSchemaElement? =
    null

  public suspend fun ChatRequest.Builder.llm(context: Context): Unit =
    Unit

  public fun greeting(context: Context): UserMessage? =
    null
}
