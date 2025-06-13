package osiris.core

import dev.langchain4j.data.message.AiMessage

public abstract class ExitCondition {
  public abstract fun Llm.shouldExit(): Boolean

  public class Default : ExitCondition() {
    override fun Llm.shouldExit(): Boolean {
      val lastMessage = response.lastOrNull() ?: return false
      if (lastMessage !is AiMessage) return false
      if (lastMessage.hasToolExecutionRequests()) return false
      return true
    }
  }
}
