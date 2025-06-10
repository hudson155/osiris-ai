package osiris.langfuseTracing

import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import java.time.Instant
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
        val batch = buildBatch(traceId, events)
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

private fun buildBatch(traceId: Uuid, events: List<Event>): BatchIngestion {
  val start = events.first() as ExecutionEvent.Start
  val end = events.last() as ExecutionEvent.End
  check(end.name == start.name)
  @Suppress("ForbiddenMethodCall")
  val now = Instant.now()
  val batch = BatchIngestion(
    batch = listOf(
      TraceCreate(
        id = Uuid.random(),
        timestamp = now,
        body = TraceCreate.Body(
          id = traceId,
          timestamp = start.at,
        ),
      ),
      SpanCreate(
        id = Uuid.random(),
        timestamp = now,
        body = SpanCreate.Body(
          id = Uuid.random(),
          traceId = traceId,
          parentObservationId = null,
          startTime = start.at,
          endTime = end.at,
          name = start.name,
        ),
      ),
    ),
  )
  return batch
}
