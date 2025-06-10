package osiris.langfuseTracing

import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.uuid.Uuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import osiris.event.Event
import osiris.event.ExecutionEvent
import osiris.langfuse.Langfuse

private const val langfuseTraces: String = "langfuseTraces"

@Suppress("UNCHECKED_CAST")
public val Langfuse.traces: ConcurrentMap<Uuid, List<Event>>
  get() = properties.getOrPut(langfuseTraces) {
    ConcurrentHashMap<Uuid, List<Event>>()
  } as ConcurrentMap<Uuid, List<Event>>

public fun Langfuse.trace(): (event: Event) -> Unit {
  val traceId = Uuid.random()
  traces[traceId] = emptyList()
  return { event ->
    when (event) {
      is ExecutionEvent.Start -> {
        traces[traceId] = listOf(event)
      }
      is ExecutionEvent.End -> {
        val events = traces.remove(traceId).orEmpty() + event
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
      else -> {
        traces.computeIfPresent(traceId) { _, events -> events + event }
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
