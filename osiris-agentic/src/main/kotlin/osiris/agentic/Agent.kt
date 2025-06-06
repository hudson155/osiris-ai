package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.onEach
import osiris.core.get
import osiris.core.llm

public abstract class Agent {
  public abstract val name: String
  internal open val description: String? = null
  protected abstract val model: ChatModel
  protected open val instructions: String? = null
  protected open val toolProviders: List<ToolProvider> = emptyList()
  protected open val responseType: KClass<*>? = null
  protected open val llm: ChatRequest.Builder.() -> Unit = {}

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
      block = llm,
    )
    flow.onEach { handleMessage(execution, it) }.get()
  }

  private fun handleMessage(execution: Execution, message: ChatMessage) {
    execution.messages += message
  }

  override fun toString(): String =
    "Agent(name=$name)"
}

@Suppress("LongParameterList")
internal class AgentImpl internal constructor(
  override val name: String,
  override val description: String?,
  override val model: ChatModel,
  override val instructions: String?,
  override val toolProviders: List<ToolProvider>,
  override val responseType: KClass<*>?,
  override val llm: ChatRequest.Builder.() -> Unit,
) : Agent()

public class AgentBuilder internal constructor(
  private val name: String,
) {
  public var description: String? = null
  public var model: ChatModel? = null
  public var instructions: String? = null
  public val tools: MutableList<ToolProvider> = mutableListOf()
  public var responseType: KClass<*>? = null
  private val blocks: MutableList<ChatRequest.Builder.() -> Unit> = mutableListOf()

  public fun llm(block: ChatRequest.Builder.() -> Unit) {
    blocks += block
  }

  internal fun build(): Agent =
    AgentImpl(
      name = name,
      description = description,
      model = requireNotNull(model) { "Agent $name must set a model." },
      instructions = instructions,
      toolProviders = tools,
      responseType = responseType,
      llm = { blocks.forEach { it() } },
    )
}

public fun agent(name: String, block: AgentBuilder.() -> Unit): Agent =
  AgentBuilder(name).apply(block).build()
