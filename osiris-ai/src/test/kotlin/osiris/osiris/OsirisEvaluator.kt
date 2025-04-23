package osiris.osiris

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import io.kotest.matchers.shouldBe
import osiris.osiris.event.get
import osiris.osiris.responseConverter.JsonResponseType
import osiris.osiris.schema.OsirisSchema

internal class OsirisEvaluator(
  model: OsirisModel,
) {
  @OsirisSchema.Name("eval")
  private data class Response(
    @OsirisSchema.Type("boolean")
    val matchesCriteria: Boolean,
  ) {
    companion object : JsonResponseType<Response>()
  }

  private val evaluator: Osiris<Response> =
    Osiris.create(model) {
      responseType = Response
    }

  suspend fun evaluate(response: String, criteria: String) {
    val langchainRequest = ChatRequest.builder()
      .messages(
        AiMessage(response),
        SystemMessage("Evaluate the LLM response according to the following criteria."),
        UserMessage(criteria),
      )
      .responseFormat(Response.format())
      .build()
    evaluator.request(langchainRequest).get().shouldBe(Response(true))
  }
}
