package osiris.langfuseTracing

import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import osiris.langfuse.Langfuse
import osiris.tracing.Span

public suspend fun Langfuse.trace(trace: List<Span<*>>) {
  val batch = BatchBuilder(trace).build()
  client.request {
    method = HttpMethod.Post
    url("ingestion")
    contentType(ContentType.Application.Json)
    accept(ContentType.Application.Json)
    setBody(batch)
  }
}
