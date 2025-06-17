package osiris.langfusePrompt

import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import osiris.langfuse.Langfuse

public suspend fun Langfuse.prompt(name: String): Prompt {
  val response = client.request {
    // https://api.reference.langfuse.com/#tag/prompts/get/api/public/v2/prompts/{promptName}
    method = HttpMethod.Get
    url("v2/prompts/$name")
    accept(ContentType.Application.Json)
  }
  return response.body<Prompt>()
}
