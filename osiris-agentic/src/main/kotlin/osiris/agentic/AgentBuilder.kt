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
  private val llmBlock: ChatRequest.Builder.() -> Unit,
) : Agent(name, model) {
  override fun ChatRequest.Builder.llm(): Unit =
    llmBlock()
}

public class AgentBuilder internal constructor(
  private val name: String,
) {
  /**
   * The description is not used by the Agent itself.
   * Rather, when other Agents consult this Agent,
   * the consulting agent is presented with this description in the consultation tool.
   */
  public var description: String? = null

  /**
   * An Agent is associated with a specific model.
   */
  public var model: ChatModel? = null

  /**
   * The instructions for this agent's LLM call.
   */
  public var instructions: Instructions? = null

  /**
   * Tools are passed to the LLM.
   */
  public val tools: MutableList<Tool<*>> = mutableListOf()

  /**
   * Class reference for structured output.
   * If not provided, output will be a string.
   */
  public var responseType: KClass<*>? = null

  private val llmBlocks: MutableList<ChatRequest.Builder.() -> Unit> = mutableListOf()

  /**
   * Use this to customize the Langchain4j chat request.
   */
  public fun llm(block: ChatRequest.Builder.() -> Unit) {
    llmBlocks += block
  }

  internal fun build(): Agent =
    AgentImpl(
      name = name,
      description = description,
      model = requireNotNull(model) { "Agent $name must set a model." },
      instructions = instructions,
      tools = tools,
      responseType = responseType,
      llmBlock = { llmBlocks.forEach { it() } },
    )
}

/**
 * Helper DSL to build an [Agent].
 */
public fun agent(
  /**
   * The Agent's name uniquely identifies it within the [Network].
   */
  name: String,

  block: AgentBuilder.() -> Unit,
): Agent =
  AgentBuilder(name).apply(block).build()
