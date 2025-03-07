package osiris.osiris

import com.openai.core.Timeout
import com.openai.models.ChatCompletionCreateParams.ServiceTier
import com.openai.models.ChatCompletionReasoningEffort
import com.openai.models.ChatCompletionTool
import com.openai.models.ChatCompletionToolChoiceOption
import com.openai.models.ChatModel

public class OsirisOptions<out Response : Any>(
  internal val exit: (state: OsirisState) -> Boolean =
    exit@{ state ->
      if (state.newMessages == 0) return@exit false
      val lastAssistantMessage = state.messages.lastOrNull { it.isAssistant() } ?: return@exit false
      return@exit lastAssistantMessage.asAssistant().content().isPresent
    },

  internal val sequentialTries: Int =
    1,

  internal val parallelTries: (state: OsirisState) -> Int =
    parallelTries@{ 1 },

  internal val model: (state: OsirisState) -> ChatModel,

  internal val responseType: (state: OsirisState) -> OsirisResponseType<Response>,

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
