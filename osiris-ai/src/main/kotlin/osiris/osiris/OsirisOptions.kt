package osiris.osiris

import com.openai.core.Timeout
import com.openai.models.ChatCompletionCreateParams.ServiceTier
import com.openai.models.ChatCompletionReasoningEffort
import com.openai.models.ChatCompletionTool
import com.openai.models.ChatCompletionToolChoiceOption
import com.openai.models.ChatModel

@Suppress("LongParameterList", "NoBlankLineInList", "UseDataClass")
public class OsirisOptions<out Response : Any>(
  /**
   * Which OpenAI model to use. This can be static or can be dynamically derived from the state.
   */
  internal val model: (state: OsirisState) -> ChatModel,

  /**
   *
   */
  internal val responseType: OsirisResponseType<Response>,

  internal val sequentialTries: Int =
    1,

  internal val parallelTries: (state: OsirisState) -> Int =
    parallelTries@{ 1 },

  /**
   * By default, Osiris will exit when the last message is an assistant message.
   * This is usually the intended behaviour.
   */
  internal val exit: (state: OsirisState) -> Boolean =
    exit@{ state ->
      val lastMessage = state.messages.lastOrNull() ?: return@exit false
      return@exit lastMessage.isAssistant()
    },

  internal val tools: (state: OsirisState) -> List<ChatCompletionTool>? =
    tools@{ null },

  internal val maxCompletionTokens: (state: OsirisState) -> Long? =
    maxCompletionTokens@{ null },

  internal val parallelToolCalls: (state: OsirisState) -> Boolean =
    parallelToolCalls@{ false },

  internal val reasoningEffort: (state: OsirisState) -> ChatCompletionReasoningEffort? =
    reasoningEffort@{ null },

  internal val serviceTier: (state: OsirisState) -> ServiceTier =
    serviceTier@{ ServiceTier.AUTO },

  internal val temperature: (state: OsirisState) -> Double? =
    temperature@{ null },

  internal val toolChoice: (state: OsirisState) -> ChatCompletionToolChoiceOption? =
    toolChoice@{ null },

  internal val topP: (state: OsirisState) -> Double? =
    topP@{ null },

  internal val user: (state: OsirisState) -> String? =
    user@{ null },

  internal val timeout: (state: OsirisState) -> Timeout =
    timeout@{ Timeout.default() },
)
