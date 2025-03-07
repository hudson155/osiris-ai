package osiris.osiris

import com.openai.client.OpenAIClientAsync
import com.openai.core.RequestOptions
import com.openai.core.Timeout
import com.openai.models.ChatCompletion
import com.openai.models.ChatCompletion.Choice.FinishReason
import com.openai.models.ChatCompletionAssistantMessageParam
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessageParam
import com.openai.models.ChatCompletionUserMessageParam
import com.openai.models.ChatModel
import com.openai.models.ResponseFormatText
import com.openai.services.async.chat.CompletionServiceAsync
import io.kotest.matchers.collections.shouldContain
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
  fun test(): Unit = runTest {
    val openAi = setup("Configure mocking") {
      val choices = listOf(
        mockk<ChatCompletion.Choice> {
          every { finishReason() } returns mockk {
            every { value() } returns FinishReason.Value.STOP
          }
          every { message() } returns mockk {
            every { toParam() } returns ChatCompletionAssistantMessageParam.builder().content("").build()
          }
        },
        mockk<ChatCompletion.Choice> {
          every { finishReason() } returns mockk {
            every { value() } returns FinishReason.Value.STOP
          }
          every { message() } returns mockk {
            every { toParam() } returns ChatCompletionAssistantMessageParam.builder().content("").build()
          }
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
      events.shouldContain(
        OsirisEvent.ChatCompletionRequest(
          params = ChatCompletionCreateParams.builder().apply {
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
          options = RequestOptions.builder().apply {
            responseValidation(true)
            timeout(Timeout.default())
          }.build(),
        )
      )
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
