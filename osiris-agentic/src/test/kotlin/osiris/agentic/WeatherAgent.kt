package osiris.agentic

import dev.langchain4j.model.chat.ChatModel
import osiris.chat.Tool
import osiris.openAi.openAi

internal object WeatherAgent : Agent("weather_agent") {
  override suspend fun model(): ChatModel =
    testModelFactory.openAi("gpt-5-nano") {
      temperature(0.20)
    }

  override suspend fun tools(): List<Tool<*>> =
    listOf(
      WeatherTool,
    )
}
