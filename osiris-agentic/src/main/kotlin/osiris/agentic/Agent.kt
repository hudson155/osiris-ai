package osiris.agentic

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kairo.reflect.KairoType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import osiris.chat.LlmState
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
) {
  /**
   * The description is not used by the Agent itself.
   * Rather, when other Agents consult this Agent,
   * the consulting Agent is presented with this description in the consultation Tool.
   */
  public open val description: String? = null
  /**
   * Type for structured output.
   * If not provided, output will be a string.
   */
  protected open val responseType: KairoType<*>? = null
  /**
   * An Agent is associated with a specific model.
   */
  public abstract suspend fun model(): ChatModel
  /**
   * The instructions for this Agent's LLM call.
   */
  protected open suspend fun instructions(): Instructions? =
    null
  /**
   * Tools are passed to the LLM.
   */
  protected open suspend fun tools(): List<Tool<*>> =
    emptyList()
  /**
   * Input guardrails asynchronously validate the agent's input, possibly throwing an exception.
   */
  protected open suspend fun inputGuardrails(): List<Guardrail> =
    emptyList()
  /**
   * Use this to customize the Langchain4j chat request.
   */
  protected open fun ChatRequest.Builder.llm(state: LlmState): Unit =
    Unit

  internal suspend fun execute() {
    val outerExecutionContext = getExecutionContext()
    val deferred = CoroutineScope(currentCoroutineContext() + SupervisorJob()).async { execute(outerExecutionContext) }
    coroutineScope { inputGuardrails().map { async { it.execute() } }.awaitAll() }
    deferred.await()
  }

  private suspend fun execute(executionContext: ExecutionContext) {
    val response = trace(
      start = { AgentEvent.Start(this, deriveText(executionContext.messages)) },
      end = { response -> AgentEvent.End(response?.let { deriveText(it) }) },
    ) {
      logger.debug { "Started Agent: (name=$name, messages=${executionContext.messages})." }
      val response = llm(
        model = model(),
        messages = buildList {
          addAll(executionContext.messages)
          instructions()?.let { add(SystemMessage(it.get())) }
        },
        tools = tools(),
        responseType = responseType,
        chatRequestBlock = { state -> llm(state) },
        exitCondition = AgentLlmExitCondition(),
      )
      logger.debug { "Ended Agent: (name=$name, response=$response)." }
      return@trace response
    }
    executionContext.state.updateAndGet { it.copy(messages = it.messages + response) }
  }

  override fun toString(): String =
    "Agent(name=$name)"
}
