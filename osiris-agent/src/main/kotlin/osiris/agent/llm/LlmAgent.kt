package osiris.agent.llm

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import osiris.agent.Agent
import osiris.agent.Context

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
    val instructions = instructions(context)
    val messages = listOfNotNull(instructions, greeting)
    chat(context, tools = emptyList(), messages = messages)
  }

  private suspend fun runLlm(context: Context) {
    val messages = buildList {
      instructions(context)?.let { add(it) }
      addAll(context.history.get())
    }
    chat(context, tools = tools(context), messages = messages)
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
      context.history.append(toolExecutionResult)
    }
  }

  // TODO: LLM retries?
  private suspend fun chat(context: Context, tools: List<Tool<*, *>>, messages: List<ChatMessage>) {
    val model = model(context)
    val aiResponse = model.chat {
      messages(messages)
      toolSpecifications(tools.map { it.specification() })
      llm(context)
    }
    context.history.append(aiResponse.aiMessage())
  }
}
