package osiris.evaluator

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import kairo.reflect.kairoType
import osiris.chat.convert
import osiris.chat.llm

/**
 * Evaluates LLM responses, enabling basic evals.
 */
public suspend fun evaluate(
  /**
   * The eval will run on this model.
   * It's best to use a reasoning model here.
   */
  model: ChatModel,
  /**
   * Should include relevant message history, including the user's question and the LLM's response.
   */
  messages: List<ChatMessage>,
  /**
   * The LLM's response is evaluated according to these criteria.
   * Can be multiple lines of text,
   * but larger criteria may also be broken up into multiple evaluations on the same response.
   */
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
    responseType = kairoType<Eval>(),
  )
  val eval = response.convert<Eval>()
  withClue(eval.failureReason) {
    eval.matchesCriteria.shouldBeTrue()
  }
}
