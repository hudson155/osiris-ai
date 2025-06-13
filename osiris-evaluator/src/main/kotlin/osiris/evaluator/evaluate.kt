package osiris.evaluator

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.flow.last
import osiris.core.convert
import osiris.core.llm
import osiris.event.messages

public suspend fun evaluate(
  model: ChatModel,
  messages: List<ChatMessage>,
  criteria: String,
) {
  val systemMessage = SystemMessage(
    """
      Evaluate whether the user's question was answered well according to the following criteria.
      
      $criteria
    """.trimIndent(),
  )
  val response = llm(
    model = model,
    messages = messages + systemMessage,
    responseType = Eval::class,
  )
  val eval = response.messages.last().convert<Eval>()
  withClue(eval.failureReason) {
    eval.matchesCriteria.shouldBeTrue()
  }
}
