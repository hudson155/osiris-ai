package osiris.agentic

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import osiris.core.llm

public class Agent(
  internal val name: String,
  private val model: ChatModel,
  private val instructions: String?,
) {
  public suspend fun execute() {
    val execution = useExecution()
    val systemMessage = SystemMessage(instructions)
    val response = llm(
      model = model,
      messages = execution.messages + systemMessage,
    )
    if (execution.network.settings.persistSystemMessages) execution.messages += systemMessage
    execution.messages += response
  }
}

public class AgentBuilder internal constructor(
  private val name: String,
) {
  public var model: ChatModel? = null
  public var instructions: String? = null

  internal fun build(): Agent {
    val model = requireNotNull(model) { "Agent $name must set a model." }
    return Agent(
      name = name,
      model = model,
      instructions = instructions,
    )
  }
}

public fun agent(name: String, block: AgentBuilder.() -> Unit): Agent =
  AgentBuilder(name).apply(block).build()
