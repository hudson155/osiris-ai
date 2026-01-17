package osiris

import org.koin.core.annotation.Single

@Single
internal class WeatherService(
  private val getWeather: GetWeather,
) : LlmAgent("weather_service") {
  context(context: Context)
  override suspend fun instructions(): Nothing? =
    null

  context(context: Context)
  override suspend fun tools(): List<Tool> =
    listOf(getWeather)
}
