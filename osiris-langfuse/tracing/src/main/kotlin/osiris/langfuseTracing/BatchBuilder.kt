package osiris.langfuseTracing

import java.time.Instant
import kotlin.uuid.Uuid
import osiris.event.AgentEvent
import osiris.event.ChatEvent
import osiris.event.Event
import osiris.event.ExecutionEvent

internal class BatchBuilder(
  private val traceId: Uuid,
  private val events: List<Event>,
) {
  @Suppress("ForbiddenMethodCall")
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
      },
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

  @Suppress("LongMethod")
  private fun events(): List<IngestionEvent<*>> {
    val stack = mutableListOf<Pair<Uuid, Event>>()
    return buildList {
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
              ),
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
              ),
            )
          }
          is ChatEvent.Start -> {
            stack += Pair(Uuid.random(), event)
          }
          is ChatEvent.End -> {
            val (id, start) = stack.removeLast()
            start as ChatEvent.Start
            add(
              GenerationCreate(
                id = Uuid.random(),
                timestamp = now,
                body = GenerationCreate.Body(
                  id = id,
                  traceId = traceId,
                  parentObservationId = stack.lastOrNull()?.first,
                  startTime = start.at,
                  endTime = end.at,
                  model = start.request.modelName(),
                ),
              ),
            )
          }
        }
      }
    }.also {
      check(stack.isEmpty())
    }
  }
}
