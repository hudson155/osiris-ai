package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import osiris.core.Tool
import osiris.core.response
import osiris.core.llm
import osiris.event.AgentEvent
import osiris.event.Event

public abstract class Agent(
  public val name: String,
  protected val model: ChatModel,
) {
  internal open val description: String? = null
  protected open val instructions: Instructions? = null
  protected open val tools: List<Tool<*, *>> = emptyList()
  protected open val responseType: KClass<*>? = null

  protected open fun ChatRequest.Builder.llm(): Unit = Unit

  @Suppress("SuspendFunWithFlowReturnType")
  public suspend fun execute(messages: List<ChatMessage>): Flow<Event> =
    with(getExecutionContext()) {
      flow {
        emit(AgentEvent.Start(this@Agent))
        llm(
          model = model,
          messages = buildList {
            addAll(messages)
            instructions?.let { add(SystemMessage(it.get())) }
          },
          tools = tools,
          responseType = responseType,
          block = { llm() },
        ).onEach(::emit).response().first()
        emit(AgentEvent.End(this@Agent))
      }
    }

  override fun toString(): String =
    "Agent(name=$name)"
}
