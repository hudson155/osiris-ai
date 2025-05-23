package osiris.testing

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import osiris.core.Osiris
import osiris.core.OsirisModel
import osiris.core.event.get
import osiris.core.responseConverter.JsonResponseType
import osiris.core.schema.OsirisSchema

public class OsirisEvaluator(
  model: OsirisModel,
) {
  @OsirisSchema.Name("eval")
  private data class Response(
    @OsirisSchema.Type("boolean")
    val matchesCriteria: Boolean,
    @OsirisSchema.Type("string")
    @OsirisSchema.Description("If the response does not match the criteria, provide the reason.")
    val failureReason: String?,
  ) {
    companion object : JsonResponseType<Response>()
  }

  private val evaluator: Osiris<Response> =
    Osiris.create(model) {
      responseType = Response
    }

  public suspend fun evaluate(response: String, criteria: String) {
    val langchainRequest = ChatRequest.builder()
      .messages(
        AiMessage(response),
        SystemMessage("Evaluate the LLM response according to the following criteria."),
        UserMessage(criteria),
      )
      .responseFormat(Response.format())
      .build()
    val evaluation = evaluator.request(langchainRequest).get()
    withClue(evaluation.failureReason) {
      evaluation.matchesCriteria.shouldBeTrue()
    }
  }
}
