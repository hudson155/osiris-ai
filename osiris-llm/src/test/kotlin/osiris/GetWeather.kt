package osiris

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import org.koin.core.annotation.Single
import osiris.schema.Structured

@Single
internal class GetWeather : Tool("get_weather") {
  internal data class Input(
    val city: String,
  )

  internal data class Output(
    val temperature: String,
    val conditions: String,
  )

  context(context: Context)
  override suspend fun getDescription(): String =
    "Get the weather for a given city."

  context(context: Context)
  override suspend fun parameters(): JsonObjectSchema =
    Structured.element<Input>() as JsonObjectSchema

  context(context: Context)
  override suspend fun execute(executionRequest: ToolExecutionRequest): String {
    val input = context.json.deserialize<Input>(executionRequest.arguments())
    val output = when (input.city) {
      "Calgary" -> Output("10 degrees Celsius", "Sunny")
      "Edmonton" -> Output("-20 degrees Celsius", "Snowing")
      else -> return "Unknown city: ${input.city}."
    }
    return context.json.serialize(output)
  }
}
