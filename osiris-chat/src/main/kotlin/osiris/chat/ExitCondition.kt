package osiris.chat

import dev.langchain4j.data.message.AiMessage

/**
 * By default ([Default]), Osiris will run LLM requests in a loop,
 * executing Tool calls until the LLM responds.
 * This means several round trips to the LLM.
 *
 * This approach should fit simple use cases.
 * If you have a more complex use case, you can implement your own [ExitCondition] instead.
 */
public fun interface ExitCondition {
  public fun shouldExit(state: LlmState): Boolean

  public class Default : ExitCondition {
    override fun shouldExit(state: LlmState): Boolean {
      val lastMessage = state.response.lastOrNull() ?: return false
      if (lastMessage !is AiMessage) return false
      if (lastMessage.hasToolExecutionRequests()) return false
      return true
    }
  }
}
