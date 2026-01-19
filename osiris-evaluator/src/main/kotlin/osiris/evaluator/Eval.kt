package osiris.evaluator

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import io.kotest.assertions.withClue
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import osiris.Context
import osiris.Model
import osiris.defaultModel
import osiris.history
import osiris.json
import osiris.schema.Structured

@Structured.Name("eval")
internal data class Eval(
  @Structured.Description("Briefly explain the rationale behind the score.")
  val explanation: String? = null, // Rationale before boolean.
  @Structured.Description("A score between 1 (the LLM responded poorly) and 10 (the LLM responded well), inclusive.")
  val score: Int,
)

/**
 * Evaluates the LLM's response against [criteria].
 */
context(context: Context)
public suspend fun evaluate(
  criteria: String,
  model: Model = requireNotNull(context.defaultModel) { "No model specified, and default model not set." },
  /**
   * LLMs don't do a consistent job at binary pass/fail without a good explanation of how strict the cutoff should be.
   * For this reason, an integer [threshold] between 1 and 10 (inclusive) is used instead of binary pass/fail.
   */
  threshold: Int = 7,
) {
  val systemMessage = SystemMessage.from(
    listOf(
      "Evaluate the LLM's response to the user's question, according to the following criteria.",
      criteria,
    ).joinToString("\n\n"),
  )
  val response = model.chat {
    messages(context.history.get() + systemMessage)
    responseFormat(
      ResponseFormat.builder().apply {
        type(ResponseFormatType.JSON)
        jsonSchema(Structured.schema<Eval>())
      }.build(),
    )
  }
  val eval = context.json.deserialize<Eval>(response.aiMessage().text())
  withClue(eval.explanation) {
    eval.score.shouldBeGreaterThanOrEqual(threshold)
  }
}
