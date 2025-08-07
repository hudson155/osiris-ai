package osiris.agentic

import dev.langchain4j.model.chat.ChatModel
import osiris.openAi.openAi
import osiris.prompt.Instructions

internal object MathAgent : Agent("math_agent") {
  override suspend fun model(): ChatModel =
    testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }

  override suspend fun instructions(): Instructions =
    Instructions { "Do the math. Return only the answer (nothing else)." }
}
