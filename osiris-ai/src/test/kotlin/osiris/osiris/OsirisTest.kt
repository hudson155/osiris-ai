package osiris.osiris

import com.openai.client.OpenAIClientAsync
import com.openai.core.Timeout
import com.openai.models.ChatCompletion
import com.openai.models.ChatCompletion.Choice.FinishReason
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessage
import com.openai.models.ChatCompletionMessageParam
import com.openai.models.ChatCompletionUserMessageParam
import com.openai.models.ChatModel
import com.openai.models.ResponseFormatText
import com.openai.services.async.chat.CompletionServiceAsync
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.CompletableFuture
import kairo.testing.postcondition
import kairo.testing.setup
import kairo.testing.test
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class OsirisTest {
  @Test
  fun `happy path`(): Unit = runTest {
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
        mockk<ChatCompletion.Choice> {
          every { finishReason() } returns FinishReason.STOP
          every { message() } returns ChatCompletionMessage.builder()
            .refusal(null)
            .content(
              "She booked the cheapest flight, hoping distance would soften the pain of what she left behind." +
                " As the plane lifted off, she stared at the city shrinking below, wondering if she’d ever return." +
                " Somewhere over the clouds, she realized she no longer cared.",
            )
            .build()
        },
      )
      return@setup mockk<OpenAIClientAsync> {
        every {
          chat()
        } returns mockk {
          every { completions() } returns mockk {
            mockChatCompletions(choices)
          }
        }
      }
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
        responseType = { OsirisResponseType.Text() },
      )
      return@test osiris.execute(messages, options).toList()
    }

    postcondition("Check events") {
      events.shouldHaveSize(3)
      events[0].should { event ->
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
            responseFormat(ChatCompletionCreateParams.ResponseFormat.ofText(ResponseFormatText.builder().build()))
            serviceTier(ChatCompletionCreateParams.ServiceTier.AUTO)
          }.build(),
        )
        event.options.responseValidation.shouldNotBeNull().shouldBeTrue()
        event.options.timeout.shouldBe(Timeout.default())
      }
      events[1].should { event ->
        event.shouldBeInstanceOf<OsirisEvent.ChatCompletionResponse>()
        event.chatCompletion.choices().should { choices ->
          choices.shouldHaveSize(2)
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
          choices[1].should { choice ->
            choice.finishReason().shouldBe(FinishReason.STOP)
            choice.message().should { message ->
              message.refusal().shouldBeEmpty()
              message.content().get().shouldBe(
                "She booked the cheapest flight, hoping distance would soften the pain of what she left behind." +
                  " As the plane lifted off, she stared at the city shrinking below, wondering if she’d ever return." +
                  " Somewhere over the clouds, she realized she no longer cared.",
              )
            }
          }
        }
      }
      events[2].should { event ->
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
  }

  private fun CompletionServiceAsync.mockChatCompletions(choices: List<ChatCompletion.Choice>) {
    val chatCompletion = mockk<ChatCompletion> {
      every { choices() } returns choices
    }
    val completableFuture = mockk<CompletableFuture<ChatCompletion>> {
      every { isDone } returns true
      every { get() } returns chatCompletion
    }
    every { create(any(), any()) } returns mockk {
      every { toCompletableFuture() } returns completableFuture
    }
  }
}
