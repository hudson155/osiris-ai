package osiris.agentic

import dev.langchain4j.data.message.UserMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlin.reflect.KClass
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import osiris.chat.convert
import osiris.openAi.openAi
import osiris.prompt.Instructions
import osiris.schema.LlmSchema
import osiris.tracing.EventLogger

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class GuardrailTest {
  internal object InputGuardrail : osiris.agentic.Agent("input_guardrail", testModelFactory.openAi("gpt-4.1-nano")) {
    @LlmSchema.SchemaName("input_guardrail")
    internal data class Output(
      val isUserAskingAboutProvincialCapitals: Boolean,
    )

    override val instructions: Instructions =
      Instructions { "Is the user asking about provincial capitals?" }

    override val responseType: KClass<Output> = Output::class
  }

  internal object Agent : osiris.agentic.Agent("agent", testModelFactory.openAi("gpt-4.1-nano")) {
    override val inputGuardrails: List<Guardrail> =
      listOf(
        Guardrail(InputGuardrail) { messages ->
          require(messages.convert<InputGuardrail.Output>().isUserAskingAboutProvincialCapitals) {
            "User is not asking about provincial capitals."
          }
        }
      )
  }

  private val network: Network =
    network("network") {
      entrypoint = Agent.name
      agents += Agent
      listener(EventLogger)
    }

  @Test
  fun `guardrail hit`(): Unit = runTest {
    shouldThrow<IllegalArgumentException> {
      network.run(
        messages = listOf(UserMessage("What's 2+2?"))
      )
    }.shouldHaveMessage("User is not asking about provincial capitals.")
  }

  @Test
  fun `guardrail not hit`(): Unit = runTest {
    val response = network.run(
      messages = listOf(UserMessage("What's the capital of Saskatchewan (province in Canada)?")),
    )
    response.convert<String>().lowercase().shouldContain("regina")
  }
}
