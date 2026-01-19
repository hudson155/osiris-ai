package osiris

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.chat.request.ResponseFormatType
import org.koin.core.annotation.Single
import osiris.schema.Structured

@Single
internal class PersonIdentifier : LlmAgent("person_identifier") {
  @Structured.Name("person")
  internal data class Output(
    val name: String,
    val age: Int,
  )

  context(context: Context)
  override suspend fun instructions(): SystemMessage =
    SystemMessage.from("Provide a JSON representation of the person matching this description.")

  context(context: Context)
  override suspend fun responseFormat(): ResponseFormat =
    ResponseFormat.builder().apply {
      type(ResponseFormatType.JSON)
      jsonSchema(Structured.schema<Output>())
    }.build()
}
