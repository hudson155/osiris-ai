package osiris.evaluator

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatModel
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import osiris.core.convert
import osiris.core.get
import osiris.core.llm

public suspend fun evaluate(
  model: ChatModel,
  response: String?,
  criteria: String,
) {
  response.shouldNotBeNull()
  @Suppress("NoNameShadowing")
  val response = llm(
    model = model,
    messages = listOf(
      AiMessage(response),
      SystemMessage("Evaluate the LLM response according to the following criteria."),
      UserMessage(criteria),
    ),
    responseType = Eval::class,
  )
  val eval = response.get().convert<Eval>().shouldNotBeNull()
  withClue(eval.failureReason) {
    eval.matchesCriteria.shouldBeTrue()
  }
}
