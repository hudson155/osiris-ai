package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kairo.reflect.KairoType
import kotlin.reflect.KClass
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import osiris.chat.Tool
import osiris.chat.deriveText
import osiris.chat.llm
import osiris.prompt.Instructions
import osiris.tracing.AgentEvent
import osiris.tracing.trace

private val logger: KLogger = KotlinLogging.logger {}

/**
 * Agents have a specific role/task,
 * and are equipped with instructions and Tools specific to that role/task.
 */
public abstract class Agent(
  /**
   * The Agent's name uniquely identifies it within the Network.
   */
  public val name: String,
  /**
   * An Agent is associated with a specific model.
   */
  protected val model: ChatModel,
) {
  /**
   * The description is not used by the Agent itself.
   * Rather, when other Agents consult this Agent,
   * the consulting Agent is presented with this description in the consultation Tool.
   */
  public open val description: String? = null
  /**
   * The instructions for this Agent's LLM call.
   */
  protected open val instructions: Instructions? = null
  /**
   * Tools are passed to the LLM.
   */
  protected open val tools: List<Tool<*>> = emptyList()
  /**
   * Type for structured output.
   * If not provided, output will be a string.
   */
  protected open val responseType: KairoType<*>? = null
  /**
   * Input guardrails asynchronously validate the agent's input, possibly throwing an exception.
   */
  protected open val inputGuardrails: List<Guardrail> = emptyList()
  /**
   * Use this to customize the Langchain4j chat request.
   */
  protected open fun ChatRequest.Builder.llm(response: List<ChatMessage>): Unit = Unit

  public suspend fun execute() {
    val outerExecutionContext = getExecutionContext()
    coroutineScope {
      buildList {
        inputGuardrails.forEach { guardrail ->
          val innerExecutionContext = outerExecutionContext.withMessages(outerExecutionContext.messages)
          add(async { withContext(innerExecutionContext) { guardrail.execute() } })
        }
        add(async { execute(outerExecutionContext) })
      }.awaitAll()
    }
  }

  private suspend fun execute(executionContext: ExecutionContext) {
    val response = trace(
      start = { AgentEvent.Start(this, deriveText(executionContext.messages)) },
      end = { response -> AgentEvent.End(response?.let { deriveText(it) }) },
    ) {
      logger.debug { "Started Agent: (name=$name, messages=${executionContext.messages})." }
      val response = llm(
        model = model,
        messages = buildList {
          addAll(executionContext.messages)
          instructions?.let { add(SystemMessage(it.get())) }
        },
        tools = tools,
        responseType = responseType,
        chatRequestBlock = { response -> llm(response) },
      )
      logger.debug { "Ended Agent: (name=$name, response=$response)." }
      return@trace response
    }
    executionContext.response += response
  }

  override fun toString(): String =
    "Agent(name=$name)"
}
