package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
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
  private val block: ChatRequest.Builder.() -> Unit,
) {
  public suspend fun execute(execution: Execution) {
    val systemMessage = instructions?.let { SystemMessage(it) }
    val messages = buildList {
      addAll(execution.messages)
      if (systemMessage != null) add(systemMessage)
    }
    val tools = toolProviders.map { it.provide(execution) }
    val flow = llm(
      model = model,
      messages = messages,
      tools = tools,
      responseType = responseType,
      block = block,
    )
    flow.onEach { handleMessage(execution, it) }.get()
  }

  private fun handleMessage(execution: Execution, message: ChatMessage) {
    execution.messages += message
  }

  override fun toString(): String =
    "Agent(name=$name)"
}

public class AgentBuilder internal constructor(
  private val name: String,
) {
  public var description: String? = null
  public var model: ChatModel? = null
  public var instructions: String? = null
  public val tools: MutableList<ToolProvider> = mutableListOf()
  public var responseType: KClass<*>? = null
  private val blocks: MutableList<ChatRequest.Builder.() -> Unit> = mutableListOf()

  public fun llm(llm: ChatRequest.Builder.() -> Unit) {
    blocks += llm
  }

  internal fun build(): Agent =
    Agent(
      name = name,
      description = description,
      model = requireNotNull(model) { "Agent $name must set a model." },
      instructions = instructions,
      toolProviders = tools,
      responseType = responseType,
      block = { blocks.forEach { it() } },
    )
}

public fun agent(name: String, block: AgentBuilder.() -> Unit): Agent =
  AgentBuilder(name).apply(block).build()
