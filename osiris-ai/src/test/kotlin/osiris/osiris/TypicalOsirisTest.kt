package osiris.osiris

import com.openai.core.Timeout
import com.openai.models.ChatCompletion
import com.openai.models.ChatCompletion.Choice.FinishReason
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionCreateParams.ResponseFormat
import com.openai.models.ChatCompletionMessage
import com.openai.models.ChatCompletionMessageParam
import com.openai.models.ChatCompletionUserMessageParam
import com.openai.models.ChatModel
import com.openai.models.ResponseFormatText
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kairo.testing.postcondition
import kairo.testing.setup
import kairo.testing.test
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class TypicalOsirisTest : OsirisTest() {
  @Test
  fun test(): Unit = runTest {
    val openAi = setup("Configure mocking") {
      val choices = listOf(
        mockk<ChatCompletion.Choice> {
          every { finishReason() } returns FinishReason.STOP
          every { message() } returns ChatCompletionMessage.builder()
            .refusal(null)
            .content(
              "The airplane shuddered as it cut through the storm," +
                " lightning flashing across the windows like frantic warnings." +
                " The pilot, gripping the controls, whispered a prayer just as the turbulence vanished," +
                " revealing a sky so clear it felt otherworldly." +
                " Below them, the ocean stretched endlessly, and not a single trace of land remained.",
            )
            .build()
        },
      )
      return@setup mockChatCompletions(choices)
    }

    val events = test("Execute Osiris") {
      val osiris = Osiris(openAi)
      val messages = listOf(
        ChatCompletionMessageParam.ofUser(
          ChatCompletionUserMessageParam.builder().content("Write a short story about an airplane.").build(),
        ),
      )
      val options = OsirisOptions(
        model = { ChatModel.GPT_4O },
        responseType = OsirisResponseType.Text(),
      )
      return@test osiris.execute(messages, options).toList()
    }

    postcondition("Check events") {
      events.shouldHaveSize(3)
      checkEvent0(events[0])
      checkEvent1(events[1])
      checkEvent2(events[2])
    }
  }

  private fun checkEvent0(event: OsirisEvent<String>) {
    event.shouldBeInstanceOf<OsirisEvent.ChatCompletionRequest>()
    event.params.shouldBe(
      ChatCompletionCreateParams.builder().apply {
        messages(
          listOf(
            ChatCompletionMessageParam.ofUser(
              ChatCompletionUserMessageParam.builder().content("Write a short story about an airplane.").build(),
            ),
          ),
        )
        model(ChatModel.GPT_4O)
        n(1)
        parallelToolCalls(false)
        responseFormat(ResponseFormat.ofText(ResponseFormatText.builder().build()))
        serviceTier(ChatCompletionCreateParams.ServiceTier.AUTO)
      }.build(),
    )
    event.options.responseValidation.shouldNotBeNull().shouldBeTrue()
    event.options.timeout.shouldBe(Timeout.default())
  }

  private fun checkEvent1(event: OsirisEvent<String>) {
    event.shouldBeInstanceOf<OsirisEvent.ChatCompletionResponse>()
    event.chatCompletion.choices().should { choices ->
      choices.shouldHaveSize(1)
      choices[0].should { choice ->
        choice.finishReason().shouldBe(FinishReason.STOP)
        choice.message().should { message ->
          message.refusal().shouldBeEmpty()
          message.content().get().shouldBe(
            "The airplane shuddered as it cut through the storm," +
              " lightning flashing across the windows like frantic warnings." +
              " The pilot, gripping the controls, whispered a prayer just as the turbulence vanished," +
              " revealing a sky so clear it felt otherworldly." +
              " Below them, the ocean stretched endlessly, and not a single trace of land remained.",
          )
        }
      }
    }
  }

  private fun checkEvent2(event: OsirisEvent<String>) {
    event.shouldBeInstanceOf<OsirisEvent.Result<String>>()
    event.result.shouldBe(
      "The airplane shuddered as it cut through the storm," +
        " lightning flashing across the windows like frantic warnings." +
        " The pilot, gripping the controls, whispered a prayer just as the turbulence vanished," +
        " revealing a sky so clear it felt otherworldly." +
        " Below them, the ocean stretched endlessly, and not a single trace of land remained.",
    )
  }
}
