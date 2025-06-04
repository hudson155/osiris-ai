package osiris.agentic

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import osiris.core.Tool
import osiris.core.llm

@Suppress("LongParameterList")
public class Agent internal constructor(
  internal val name: String,
  private val model: ChatModel,
  private val instructions: String?,
  private val tools: List<Tool<*, *>>,
) {
  public suspend fun execute() {
    val execution = useExecution()
    val systemMessage = instructions?.let { SystemMessage(it) }
    val messages = buildList {
      addAll(execution.messages)
      if (systemMessage != null) add(systemMessage)
    }
    val response = llm(
      model = model,
      messages = messages,
      tools = tools,
    )
    if (execution.network.settings.persistSystemMessages && systemMessage != null) {
      execution.messages += systemMessage
    }
    execution.messages += response
  }
}

public class AgentBuilder internal constructor(
  private val name: String,
) {
  public var model: ChatModel? = null
  public var instructions: String? = null
  public val tools: MutableList<Tool<*, *>> = mutableListOf()

  internal fun build(): Agent {
    val model = requireNotNull(model) { "Agent $name must set a model." }
    return Agent(
      name = name,
      model = model,
      instructions = instructions,
      tools = tools,
    )
  }
}

public fun agent(name: String, block: AgentBuilder.() -> Unit): Agent =
  AgentBuilder(name).apply(block).build()
