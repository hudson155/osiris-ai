package osiris.langfuseTracing

import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlin.uuid.Uuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import osiris.event.Event
import osiris.event.ExecutionEvent
import osiris.langfuse.Langfuse

public fun Langfuse.trace(): (event: Event) -> Unit {
  val traceId = Uuid.random()
  val events: MutableList<Event> = mutableListOf()
  return { event ->
    events += event
    if (event is ExecutionEvent.End) {
      CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        val batch = BatchBuilder(traceId, events).build()
        client.request {
          method = HttpMethod.Post
          url("ingestion")
          contentType(ContentType.Application.Json)
          accept(ContentType.Application.Json)
          setBody(batch)
        }
      }
    }
  }
}
