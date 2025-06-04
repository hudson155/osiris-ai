package osiris.core

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.model.chat.request.ChatRequest

public abstract class ExitCondition {
  public abstract fun evaluate(chatRequest: ChatRequest): Boolean

  public class Default : ExitCondition() {
    private var count: Int = 0

    override fun evaluate(chatRequest: ChatRequest): Boolean {
      count++
      if (count <= 1) return false
      val lastMessage = chatRequest.messages().lastOrNull()
      return lastMessage is AiMessage && !lastMessage.hasToolExecutionRequests()
    }
  }
}
