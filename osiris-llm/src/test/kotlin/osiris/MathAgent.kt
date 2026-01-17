package osiris

import dev.langchain4j.data.message.SystemMessage
import org.koin.core.annotation.Single

@Single
internal class MathAgent(
  private val modelFactory: ModelFactory,
) : Agent(name = "math_agent") {
  context(context: Context)
  override suspend fun execute() {
    val model = modelFactory.default()
    val response = model.chat {
      messages(
        context.history.get() +
          SystemMessage("Do the math. Return only the answer (nothing else).")
      )
    }
    context.history.append(response.aiMessage())
  }
}
