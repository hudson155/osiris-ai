package osiris

import dev.langchain4j.data.message.ChatMessage
import org.koin.core.annotation.Single

@Single
internal class WeatherService(
  private val getWeather: GetWeather,
) : LlmAgent("weather_service") {
  context(context: Context)
  override suspend fun instructions(): List<ChatMessage> =
    emptyList()

  context(context: Context)
  override suspend fun tools(): List<Tool> =
    listOf(getWeather)
}
