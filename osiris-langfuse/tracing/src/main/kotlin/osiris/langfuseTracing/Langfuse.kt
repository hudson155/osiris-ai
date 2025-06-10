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
import osiris.event.AgentEvent
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

internal class BatchBuilder(
  private val traceId: Uuid,
  private val events: List<Event>,
) {
  private val now: Instant = Instant.now()

  private val start: ExecutionEvent.Start = events.first() as ExecutionEvent.Start
  private val end: ExecutionEvent.End = events.last() as ExecutionEvent.End

  init {
    check(events.filterIsInstance<ExecutionEvent.Start>().size == 1)
    check(events.filterIsInstance<ExecutionEvent.End>().size == 1)
    check(end.name == start.name)
  }

  fun build(): BatchIngestion =
    BatchIngestion(
      batch = buildList {
        add(traceCreate())
        addAll(events())
      }
    )

  private fun traceCreate(): TraceCreate =
    TraceCreate(
      id = Uuid.random(),
      timestamp = now,
      body = TraceCreate.Body(
        id = traceId,
        timestamp = start.at,
      ),
    )

  private fun events(): List<IngestionEvent<*>> {
    val stack = mutableListOf<Pair<Uuid, Event>>()
    return buildList<IngestionEvent<*>> {
      events.forEach { event ->
        when (event) {
          is ExecutionEvent.Start -> {
            stack += Pair(Uuid.random(), event)
          }
          is ExecutionEvent.End -> {
            val (id, start) = stack.removeLast()
            start as ExecutionEvent.Start
            add(
              SpanCreate(
                id = Uuid.random(),
                timestamp = now,
                body = SpanCreate.Body(
                  id = id,
                  traceId = traceId,
                  parentObservationId = stack.lastOrNull()?.first,
                  startTime = start.at,
                  endTime = end.at,
                  name = start.name,
                ),
              )
            )
          }
          is AgentEvent.Start -> {
            stack += Pair(Uuid.random(), event)
          }
          is AgentEvent.End -> {
            val (id, start) = stack.removeLast()
            start as AgentEvent.Start
            add(
              SpanCreate(
                id = Uuid.random(),
                timestamp = now,
                body = SpanCreate.Body(
                  id = id,
                  traceId = traceId,
                  parentObservationId = stack.lastOrNull()?.first,
                  startTime = start.at,
                  endTime = end.at,
                  name = start.agent.name,
                ),
              )
            )
          }
          else -> Unit
        }
      }
    }.also {
      check(stack.isEmpty())
    }
  }
}
