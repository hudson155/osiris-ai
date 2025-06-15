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
import osiris.tracing.AgentEvent
import osiris.tracing.trace

private val logger: KLogger = KotlinLogging.logger {}

public abstract class Agent(
  public val name: String,
  protected val model: ChatModel,
) {
  internal open val description: String? = null
  protected open val instructions: Instructions? = null
  protected open val tools: List<Tool<*>> = emptyList()
  protected open val responseType: KClass<*>? = null

  protected open fun ChatRequest.Builder.llm(): Unit = Unit

  public suspend fun execute(messages: List<ChatMessage>): List<ChatMessage> =
    trace({ AgentEvent.Start(this, deriveText(messages)) }, { AgentEvent.End(deriveText(it)) }) {
      logger.debug { "Started agent: (name=$name, messages=$messages)." }
      return@trace llm(
        model = model,
        messages = buildList {
          addAll(messages)
          instructions?.let { add(SystemMessage(it.get())) }
        },
        tools = tools,
        responseType = responseType,
        chatRequestBlock = { llm() },
      ).also { response ->
        logger.debug { "Ended agent: (name=$name, response=$response)." }
      }
    }

  override fun toString(): String =
    "Agent(name=$name)"
}
