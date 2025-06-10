package osiris.agentic

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.onEach
import osiris.core.get
import osiris.core.llm
import osiris.event.ChatMessageEvent
import osiris.event.Event

public abstract class Agent(
  public val name: String,
  protected val model: ChatModel,
) {
  internal open val description: String? = null
  protected open val instructions: Instructions? = null
  protected open val toolProviders: List<ToolProvider> = emptyList()
  protected open val responseType: KClass<*>? = null

  protected open fun ChatRequest.Builder.llm(): Unit = Unit

  public suspend fun execute(execution: Execution) {
    val systemMessage = instructions?.let { SystemMessage(it.get()) }
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
      block = { llm() },
    )
    flow.onEach { handleMessage(execution, it) }.get()
  }

  private fun handleMessage(execution: Execution, message: Event) {
    when (message) {
      is ChatMessageEvent -> {
        execution.messages += message.message
      }
    }
  }

  override fun toString(): String =
    "Agent(name=$name)"
}
