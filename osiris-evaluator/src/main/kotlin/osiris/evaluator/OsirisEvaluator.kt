package osiris.evaluator

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatModel
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.flow.toList
import osiris.core.osiris
import osiris.schema.OsirisSchema
import osiris.testing.getResponse

public class OsirisEvaluator(
  private val model: ChatModel,
) {
  @OsirisSchema.SchemaName("eval")
  private data class Eval(
    val matchesCriteria: Boolean,
    @OsirisSchema.Description("If the response does not match the criteria, provide the reason.")
    val failureReason: String?,
  )

  public suspend fun evaluate(response: String, criteria: String) {
    val osirisEvents = osiris<Eval>(
      model = model,
      messages = listOf(
        AiMessage(response),
        SystemMessage("Evaluate the LLM response according to the following criteria."),
        UserMessage(criteria),
      ),
    ).toList()
    val eval = osirisEvents.getResponse().shouldNotBeNull()
    withClue(eval.failureReason) {
      eval.matchesCriteria.shouldBeTrue()
    }
  }
}
