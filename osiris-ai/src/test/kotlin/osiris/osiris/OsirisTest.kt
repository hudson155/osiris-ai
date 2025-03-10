package osiris.osiris

import com.openai.client.OpenAIClientAsync
import com.openai.models.ChatCompletion
import com.openai.services.async.chat.CompletionServiceAsync
import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.CompletableFuture

internal abstract class OsirisTest {
  protected fun mockChatCompletions(choices: List<ChatCompletion.Choice>): OpenAIClientAsync =
    mockk<OpenAIClientAsync> {
      every {
        chat()
      } returns mockk {
        every { completions() } returns mockk {
          mockChatCompletions(choices)
        }
      }
    }

  protected fun CompletionServiceAsync.mockChatCompletions(choices: List<ChatCompletion.Choice>) {
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
