package osiris.agentic

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import kotlin.reflect.KClass
import osiris.core.Tool

@Suppress("LongParameterList")
internal class AgentImpl(
  name: String,
  override val description: String?,
  model: ChatModel,
  override val instructions: Instructions?,
  override val tools: List<Tool<*>>,
  override val responseType: KClass<*>?,
  private val chatRequestBlock: ChatRequest.Builder.() -> Unit,
) : Agent(name, model) {
  override fun ChatRequest.Builder.chatRequest(): Unit =
    chatRequestBlock()
}

public class AgentBuilder internal constructor(
  private val name: String,
) {
  public var description: String? = null
  public var model: ChatModel? = null
  public var instructions: Instructions? = null
  public val tools: MutableList<Tool<*>> = mutableListOf()
  public var responseType: KClass<*>? = null
  private val chatRequestBlocks: MutableList<ChatRequest.Builder.() -> Unit> = mutableListOf()

  public fun chatRequest(block: ChatRequest.Builder.() -> Unit) {
    chatRequestBlocks += block
  }

  internal fun build(): Agent =
    AgentImpl(
      name = name,
      description = description,
      model = requireNotNull(model) { "Agent $name must set a model." },
      instructions = instructions,
      tools = tools,
      responseType = responseType,
      chatRequestBlock = { chatRequestBlocks.forEach { it() } },
    )
}

public fun agent(name: String, block: AgentBuilder.() -> Unit): Agent =
  AgentBuilder(name).apply(block).build()
