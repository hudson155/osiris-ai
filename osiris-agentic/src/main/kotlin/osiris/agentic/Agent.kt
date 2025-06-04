package osiris.agentic

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.onEach
import osiris.core.get
import osiris.core.llm

@Suppress("LongParameterList")
public class Agent internal constructor(
  internal val name: String,
  internal val description: String?,
  private val model: ChatModel,
  private val instructions: String?,
  private val toolProviders: List<ToolProvider>,
  private val responseType: KClass<*>?,
) {
  public suspend fun execute(execution: Execution) {
    val systemMessage = instructions?.let { SystemMessage(it) }
    val messages = buildList {
      addAll(execution.messages)
      if (systemMessage != null) add(systemMessage)
    }
    val tools = toolProviders.map { it.provide(execution) }
    val response = llm(
      model = model,
      messages = messages,
      tools = tools,
      responseType = responseType,
    )
      .onEach { execution.messages += it }
      .get()
  }
}

public class AgentBuilder internal constructor(
  private val name: String,
) {
  public var description: String? = null
  public var model: ChatModel? = null
  public var instructions: String? = null
  public val tools: MutableList<ToolProvider> = mutableListOf()
  public var responseType: KClass<*>? = null

  internal fun build(): Agent {
    val model = requireNotNull(model) { "Agent $name must set a model." }
    return Agent(
      name = name,
      description = description,
      model = model,
      instructions = instructions,
      toolProviders = tools,
      responseType = responseType,
    )
  }
}

public fun agent(name: String, block: AgentBuilder.() -> Unit): Agent =
  AgentBuilder(name).apply(block).build()
