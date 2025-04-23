package osiris.osiris

import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import osiris.osiris.event.get

internal abstract class OsirisTest<out Response : Any> {
  abstract val targetModels: List<OsirisModel>
  abstract val evalModel: OsirisModel

  abstract val testMessages: List<OsirisTestMessage<Response>>

  @TestFactory
  fun tests(): List<DynamicTest> = runBlocking {
    targetModels.flatMap { model ->
      val osiris = buildOsiris(model)
      return@flatMap testMessages.flatMap { testMessage ->
        val langchainRequest = ChatRequest.builder()
          .messages(listOf(UserMessage(testMessage.request)))
          .build()
        val response = osiris.request(langchainRequest).get()
        return@flatMap testMessage.evals.map { eval ->
          val testName = getTestName(model, testMessage, eval)
          return@map DynamicTest.dynamicTest(testName) {
            runTest {
              evaluate(response, eval)
            }
          }
        }
      }
    }
  }

  private fun getTestName(
    model: OsirisModel,
    testMessage: OsirisTestMessage<Response>,
    eval: OsirisEval<Response>,
  ): String =
    listOf(
      model.name,
      testMessage.request,
      when (eval) {
        is OsirisEval.Criteria -> eval.criteria
        is OsirisEval.Equality<Response> -> eval.expected
      },
    ).joinToString(" > ")

  private suspend fun evaluate(response: Response, eval: OsirisEval<Response>) {
    when (eval) {
      is OsirisEval.Criteria -> evaluate(response, eval)
      is OsirisEval.Equality<Response> -> evaluate(response, eval)
    }
  }

  private suspend fun evaluate(response: Response, eval: OsirisEval.Criteria) {
    val evaluator = Osiris.create<String>(evalModel)
    val messages = listOf(
      UserMessage(
        """
          Evaluate this LLM response according to the following criteria.
          Return only "true" or "false".
          <response>
          $response
          </response>
          <criteria>
          ${eval.criteria}
          </criteria>
        """.trimIndent(),
      ),
    )
    val langchainRequest = ChatRequest.builder()
      .messages(messages)
      .build()
    evaluator.request(langchainRequest).get().shouldBe("true") // TODO: Use structured output.
  }

  private fun evaluate(response: Response, eval: OsirisEval.Equality<Response>) {
    response.shouldBe(eval.expected)
  }

  abstract fun buildOsiris(model: OsirisModel): Osiris<Response>
}
