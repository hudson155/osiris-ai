package osiris.evaluator

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.flow.first
import osiris.core.convert
import osiris.core.llm
import osiris.core.response

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
  val eval = response.response().first().convert<Eval>().shouldNotBeNull()
  withClue(eval.failureReason) {
    eval.matchesCriteria.shouldBeTrue()
  }
}
