@file:Suppress("TestInProductSource")

package osiris.testing

import dev.langchain4j.model.chat.ChatModel
import io.kotest.matchers.shouldBe
import kairo.serialization.util.writeValueAsStringSpecial
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import osiris.core.Osiris
import osiris.core.event.get
import osiris.core.osirisMapper

public abstract class OsirisTest<out Response : Any> {
  protected abstract val targetModels: List<ChatModel>
  protected abstract val evalModel: ChatModel

  protected abstract val testMessages: List<OsirisTestMessage<Response>>

  @TestFactory
  public fun tests(): List<DynamicTest> = runBlocking {
    targetModels.flatMap { model ->
      val osiris = buildOsiris(model)
      return@flatMap testMessages.flatMap { testMessage ->
        val response = osiris.request(testMessage.request).get()
        return@flatMap testMessage.evals.map { eval ->
          val testName = testName(model, testMessage, eval)
          return@map DynamicTest.dynamicTest(testName) {
            runTest {
              evaluate(response, eval)
            }
          }
        }
      }
    }
  }

  private fun testName(
    model: ChatModel,
    testMessage: OsirisTestMessage<Response>,
    eval: OsirisEval<Response>,
  ): String =
    listOf(
      model.defaultRequestParameters().modelName(),
      testMessage.name,
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
    val evaluator = OsirisEvaluator(evalModel)
    evaluator.evaluate(osirisMapper.writeValueAsStringSpecial(response), eval.criteria)
  }

  private fun evaluate(response: Response, eval: OsirisEval.Equality<Response>) {
    response.shouldBe(eval.expected)
  }

  protected abstract fun buildOsiris(model: ChatModel): Osiris<Response>
}
