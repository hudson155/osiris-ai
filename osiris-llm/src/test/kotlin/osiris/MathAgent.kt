package osiris

import dev.langchain4j.data.message.SystemMessage
import org.koin.core.annotation.Single

@Single
internal class MathAgent : LlmAgent(name = "math_agent") {
  context(context: Context)
  override suspend fun instructions(): SystemMessage =
    SystemMessage("Do the math. Return only the answer (nothing else).")
}
