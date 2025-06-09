package osiris.langfusePrompt

import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import kotlin.uuid.Uuid
import osiris.langfuse.Langfuse

public data class Prompt(
  val id: Uuid,
  val name: String,
  val prompt: String,
)

public suspend fun Langfuse.prompt(name: String): Prompt {
  val response = client.request {
    method = HttpMethod.Get
    url("prompts/$name")
    accept(ContentType.Application.Json)
  }
  return response.body<Prompt>()
}
