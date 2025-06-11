package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import kotlin.reflect.KClass
import osiris.core.Tool
import osiris.core.getTraceContext
import osiris.core.llm
import osiris.core.trace
import osiris.span.AgentEvent
import osiris.span.deriveText

public abstract class Agent(
  public val name: String,
  protected val model: ChatModel,
) {
  internal open val description: String? = null
  protected open val instructions: Instructions? = null
  protected open val tools: List<Tool<*, *>> = emptyList()
  protected open val responseType: KClass<*>? = null

  protected open fun ChatRequest.Builder.llm(): Unit = Unit

  public suspend fun execute(messages: List<ChatMessage>): List<ChatMessage> =
    with(getTraceContext()) {
      trace({ AgentEvent(this@Agent, deriveText(messages), deriveText(it)) }) {
        val (response) = llm(
          model = model,
          messages = buildList {
            addAll(messages)
            instructions?.let { add(SystemMessage(it.get())) }
          },
          tools = tools,
          responseType = responseType,
          block = { llm() },
        )
        return@trace response
      }
    }

  override fun toString(): String =
    "Agent(name=$name)"
}
