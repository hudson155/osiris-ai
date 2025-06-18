package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass
import osiris.core.Tool
import osiris.core.deriveText
import osiris.core.llm
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
   * Class reference for structured output.
   * If not provided, output will be a string.
   */
  protected open val responseType: KClass<*>? = null

  /**
   * Use this to customize the Langchain4j chat request.
   */
  protected open fun ChatRequest.Builder.llm(): Unit = Unit

  public suspend fun execute(messages: List<ChatMessage>): List<ChatMessage> =
    trace({ AgentEvent.Start(this, deriveText(messages)) }, { AgentEvent.End(deriveText(it)) }) {
      logger.debug { "Started Agent: (name=$name, messages=$messages)." }
      val response = llm(
        model = model,
        messages = buildList {
          addAll(messages)
          instructions?.let { add(SystemMessage(it.get())) }
        },
        tools = tools,
        responseType = responseType,
        chatRequestBlock = { llm() },
      )
      logger.debug { "Ended Agent: (name=$name, response=$response)." }
      return@trace response
    }

  override fun toString(): String =
    "Agent(name=$name)"
}
