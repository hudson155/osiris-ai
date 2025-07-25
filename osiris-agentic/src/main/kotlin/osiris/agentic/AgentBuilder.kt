package osiris.agentic

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import kairo.reflect.KairoType
import osiris.chat.LlmState
import osiris.chat.Tool
import osiris.prompt.Instructions

@Suppress("LongParameterList")
internal class AgentImpl(
  name: String,
  override val description: String?,
  model: ChatModel,
  override val instructions: Instructions?,
  override val tools: List<Tool<*>>,
  override val responseType: KairoType<*>?,
  override val inputGuardrails: List<Guardrail>,
  private val llmBlock: ChatRequest.Builder.(state: LlmState) -> Unit,
) : Agent(name, model) {
  override fun ChatRequest.Builder.llm(state: LlmState): Unit =
    llmBlock(state)
}

public class AgentBuilder internal constructor(
  private val name: String,
) {
  public var description: String? = null
  public var model: ChatModel? = null
  public var instructions: Instructions? = null
  public val tools: MutableList<Tool<*>> = mutableListOf()
  public var responseType: KairoType<*>? = null
  public val inputGuardrails: MutableList<Guardrail> = mutableListOf()
  private val llmBlocks: MutableList<ChatRequest.Builder.(state: LlmState) -> Unit> = mutableListOf()

  public fun llm(block: ChatRequest.Builder.(state: LlmState) -> Unit) {
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
      inputGuardrails = inputGuardrails,
      llmBlock = { response -> llmBlocks.forEach { it(response) } },
    )
}

/**
 * Helper DSL to build an [Agent].
 */
public fun agent(name: String, block: AgentBuilder.() -> Unit): Agent =
  AgentBuilder(name).apply(block).build()
