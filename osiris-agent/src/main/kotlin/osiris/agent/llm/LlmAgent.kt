package osiris.agent.llm

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import dev.langchain4j.model.chat.request.json.JsonSchema
import dev.langchain4j.model.chat.request.json.JsonSchemaElement
import osiris.agent.Agent
import osiris.agent.Context
import osiris.agent.history.history
import osiris.agent.tool.Tool
import osiris.agent.tool.specification

public abstract class LlmAgent(name: String) : Agent(name), LlmAgentConfig {
  final override suspend fun run(context: Context) {
    while (true) { // TODO: Limit how many iterations.
      val history = context.history.get()
      val action = LlmAction.fromHistory(history)
      when (action) {
        LlmAction.Greet -> runGreet(context)
        LlmAction.Llm -> runLlm(context)
        LlmAction.Tools -> runTools(context)
        LlmAction.User -> break
      }
    }
  }

  private suspend fun runGreet(context: Context) {
    val greeting = greeting(context) ?: return
    val messages = instructions(context) + greeting
    chat(
      context = context,
      tools = emptyList(),
      schema = schema(context),
      messages = messages,
    )
  }

  private suspend fun runLlm(context: Context) {
    val messages = instructions(context) + context.history.get()
    chat(
      context = context,
      tools = tools(context),
      schema = schema(context),
      messages = messages,
    )
  }

  private suspend fun runTools(context: Context) {
    val tools = tools(context)
    val toolExecutionRequests = (context.history.get().last() as AiMessage).toolExecutionRequests()
    // TODO: Parallel tool execution.
    toolExecutionRequests.forEach { toolExecutionRequest ->
      val tool = tools.singleNullOrThrow { it.name == toolExecutionRequest.name() }
      checkNotNull(tool) { "Tool not found: (name=${toolExecutionRequest.name()})." }
      val toolInput = toolExecutionRequest.arguments()
      val toolOutput = tool.run(context, toolInput)
      val toolExecutionResult = ToolExecutionResultMessage.from(toolExecutionRequest, toolOutput)
      context.history.append(toolExecutionResult, this)
    }
  }

  // TODO: LLM retries?
  @Suppress("LongParameterList")
  private suspend fun chat(
    context: Context,
    tools: List<Tool<*, *>>,
    schema: JsonSchemaElement?,
    messages: List<ChatMessage>,
  ) {
    val model = model(context)
    val aiResponse = model.chat {
      messages(messages)
      toolSpecifications(tools.map { it.specification() })
      if (schema != null) {
        responseFormat(
          ResponseFormat.builder().apply {
            type(ResponseFormatType.JSON)
            jsonSchema(
              JsonSchema.builder().apply {
                name(name)
                rootElement(schema)
              }.build(),
            )
          }.build(),
        )
      }
      llm(context)
    }
    context.history.append(aiResponse.aiMessage(), this)
  }
}
