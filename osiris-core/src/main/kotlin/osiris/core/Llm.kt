package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonSchema
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import osiris.event.Event
import osiris.event.MessageEvent
import osiris.schema.LlmSchema

private val logger: KLogger = KotlinLogging.logger {}

@Suppress("LongParameterList")
public class Llm(
  public val model: ChatModel,
  public val modelBlock: ChatRequest.Builder.() -> Unit,
  public val messages: List<ChatMessage>,
  public val tools: List<Tool<*>>,
  public val responseType: KClass<*>?,
  public val toolExecutor: ToolExecutor,
  public val exitCondition: ExitCondition,
) {
  public val response: MutableList<ChatMessage> = mutableListOf()

  public fun execute(): Flow<Event> =
    channelFlow {
      logger.debug { "Started LLM." }
      while (with(exitCondition) { !shouldExit() }) {
        val chatRequest = buildChatRequest()
        val lastMessage = chatRequest.messages().lastOrNull()
        logger.debug { "Last message: ${lastMessage ?: "null"}." }
        if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
          executeTools(lastMessage)
        } else {
          chat(chatRequest)
        }
      }
      logger.debug { "Ended LLM." }
    }

  @Suppress("SuspendFunWithCoroutineScopeReceiver")
  private suspend fun ProducerScope<Event>.executeTools(lastMessage: AiMessage) {
    val executionRequests = lastMessage.toolExecutionRequests()
    logger.debug { "Tool execution requests: $executionRequests." }
    val executionResults = mutableListOf<ToolExecutionResultMessage>()
    toolExecutor.execute(tools, executionRequests)
      .onEach { event ->
        if (event !is MessageEvent) return@onEach
        if (event.message !is ToolExecutionResultMessage) return@onEach
        if (event.message.id() !in executionRequests.map { it.id() }) return@onEach
        executionResults += event.message
      }
      .onCompletion {
        check(executionResults.size == executionRequests.size)
        logger.debug { "Tool execution responses: $executionResults." }
        response += executionResults
      }
      .collect { event ->
        send(event)
      }
  }

  @Suppress("SuspendFunWithCoroutineScopeReceiver")
  private suspend fun ProducerScope<Event>.chat(chatRequest: ChatRequest) {
    logger.debug { "Chat request: $chatRequest." }
    val chatResponse = model.chat(chatRequest)
    logger.debug { "Chat response: $chatResponse." }
    val aiMessage = chatResponse.aiMessage()
    send(MessageEvent(aiMessage))
    response += aiMessage
  }

  private suspend fun buildChatRequest(): ChatRequest =
    ChatRequest.builder().apply {
      messages(messages + response)
      if (tools.isNotEmpty()) {
        toolSpecifications(tools.map { it.toolSpecification.get() })
      }
      if (responseType != null) {
        val jsonSchema = JsonSchema.builder().apply {
          name(LlmSchema.generateName(responseType))
          rootElement(LlmSchema.generate(responseType))
        }.build()
        val responseFormat = ResponseFormat.builder().apply {
          type(ResponseFormatType.JSON)
          jsonSchema(jsonSchema)
        }.build()
        responseFormat(responseFormat)
      }
      modelBlock()
    }.build()
}

@Suppress("LongParameterList")
public fun llm(
  model: ChatModel,
  modelBlock: ChatRequest.Builder.() -> Unit = {},
  messages: List<ChatMessage>,
  tools: List<Tool<*>> = emptyList(),
  responseType: KClass<*>? = null,
  toolExecutor: ToolExecutor = ToolExecutor.Dispatcher(),
  exitCondition: ExitCondition = ExitCondition.Default(),
): Flow<Event> {
  require(messages.isNotEmpty()) { "Messages cannot be empty." }
  val llm = Llm(
    model = model,
    modelBlock = modelBlock,
    messages = messages,
    tools = tools,
    responseType = responseType,
    toolExecutor = toolExecutor,
    exitCondition = exitCondition,
  )
  return llm.execute()
}
