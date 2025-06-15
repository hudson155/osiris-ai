package osiris.core2

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonSchema
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import osiris.core2.ToolExecutor
import osiris.schema.LlmSchema

private val logger: KLogger = KotlinLogging.logger {}

internal class Llm(
  private val model: ChatModel,
  private val messages: List<ChatMessage>,
  val tools: List<Tool<*>>,
  val responseType: KClass<*>?,
  val chatRequestBlock: ChatRequest.Builder.() -> Unit,
  val toolExecutor: ToolExecutor,
  val exitCondition: ExitCondition,
) {
  suspend fun execute(): List<ChatMessage> {
    logger.debug { "Started LLM." }
    val response: MutableList<ChatMessage> = mutableListOf()
    while (true) {
      val messages = messages + response
      val lastMessage = messages.lastOrNull()
      logger.debug { "Last message: ${lastMessage ?: "null"}." }
      if (exitCondition.shouldExit(response)) break
      if (lastMessage is AiMessage && lastMessage.hasToolExecutionRequests()) {
        val executionRequests = lastMessage.toolExecutionRequests()
        response += executeTools(executionRequests)
      } else {
        val chatRequest = buildChatRequest(messages)
        response += chat(chatRequest)
      }
    }
    logger.debug { "Ended LLM." }
    return response
  }

  private fun executeTools(executionRequests: List<ToolExecutionRequest>): List<ChatMessage> {
    logger.debug { "Tool execution requests: $executionRequests." }
    val executionResults = toolExecutor.execute(tools, executionRequests)
    logger.debug { "Tool execution results: $executionResults." }
    return executionResults
  }

  private fun chat(chatRequest: ChatRequest): List<ChatMessage> {
    logger.debug { "Chat request: $chatRequest." }
    val chatResponse = model.chat(chatRequest)
    logger.debug { "Chat response: $chatResponse." }
    val aiMessage = chatResponse.aiMessage()
    return listOf(aiMessage)
  }

  private suspend fun buildChatRequest(messages: List<ChatMessage>): ChatRequest =
    ChatRequest.builder().apply {
      messages(messages)
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
      chatRequestBlock()
    }.build()
}

/**
 * The primary entrypoint.
 *
 * By default, Osiris will run LLM requests in a loop,
 * executing tool calls until the LLM responds.
 *
 * For more information, refer to the documentation.
 */
@Suppress("LongParameterList")
public suspend fun llm(
  /**
   * Any Langchain4j model.
   */
  model: ChatModel,
  /**
   * The initial Langchain4j messages.
   */
  messages: List<ChatMessage>,
  /**
   * The LLM can consult these tools.
   */
  tools: List<Tool<*>> = emptyList(),
  /**
   * Class reference for structured output.
   * If not provided, output will be a string.
   */
  responseType: KClass<*>? = null,
  /**
   * Use this to customize the Langchain4j chat request.
   */
  chatRequestBlock: ChatRequest.Builder.() -> Unit = {},
  /**
   * By default, tools are executed in parallel on [Dispatchers.IO] using [ToolExecutor.Dispatcher].
   */
  toolExecutor: ToolExecutor = ToolExecutor.Dispatcher(),
  /**
   * By default, Osiris will run LLM requests in a loop,
   * executing tool calls until the LLM responds.
   */
  exitCondition: ExitCondition = ExitCondition.Default(),
): List<ChatMessage> {
  require(messages.isNotEmpty()) { "Messages cannot be empty." }
  val llm = Llm(
    model = model,
    messages = messages,
    tools = tools,
    responseType = responseType,
    chatRequestBlock = chatRequestBlock,
    toolExecutor = toolExecutor,
    exitCondition = exitCondition,
  )
  return llm.execute()
}
