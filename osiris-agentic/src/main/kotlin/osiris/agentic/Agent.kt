package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import osiris.core.Tool
import osiris.core.llm
import osiris.event.Event
import osiris.event.MessageEvent

private val logger: KLogger = KotlinLogging.logger {}

public abstract class Agent(
  public val name: String,
  protected val model: ChatModel,
) {
  internal open val description: String? = null
  protected open val instructions: Instructions? = null
  protected open val tools: List<Tool<*>> = emptyList()
  protected open val responseType: KClass<*>? = null

  protected open fun ChatRequest.Builder.chatRequest(): Unit = Unit

  public suspend fun execute(messages: List<ChatMessage>): Flow<Event> {
    logger.debug { "Started agent: (name=$name, messages=$messages)." }
    val flow = llm(
      model = model,
      messages = buildList {
        addAll(messages)
        instructions?.let { add(SystemMessage(it.get())) }
      },
      tools = tools,
      responseType = responseType,
      chatRequestBlock = { chatRequest() },
    )
    var response: ChatMessage? = null
    return flow
      .onEach { event ->
        if (event !is MessageEvent) return@onEach
        response = event.message
      }
      .onCompletion { logger.debug { "Ended agent: (name=$name, response=$response)." } }
  }

  override fun toString(): String =
    "Agent(name=$name)"
}
