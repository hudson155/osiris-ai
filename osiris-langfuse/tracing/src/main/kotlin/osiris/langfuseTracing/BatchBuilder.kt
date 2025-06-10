package osiris.langfuseTracing

import java.time.Instant
import kotlin.uuid.Uuid
import osiris.event.AgentEvent
import osiris.event.ChatEvent
import osiris.event.Event
import osiris.event.ExecutionEvent

internal class BatchBuilder(
  private val traceId: Uuid,
  events: List<Event>,
) {
  @Suppress("ForbiddenMethodCall")
  private val now: Instant = Instant.now()

  private val start: ExecutionEvent.Start = events.first() as ExecutionEvent.Start
  private val end: ExecutionEvent.End = events.last() as ExecutionEvent.End

  private val events: List<Event> = events.drop(1).dropLast(1)

  init {
    check(events.filterIsInstance<ExecutionEvent.Start>().size == 1)
    check(events.filterIsInstance<ExecutionEvent.End>().size == 1)
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
        name = "Trace: ${start.network.name}",
        input = start.input,
        output = end.output,
      ),
    )

  @Suppress("LongMethod")
  private fun events(): List<IngestionEvent<*>> {
    val stack = mutableListOf<Pair<Uuid, Event>>()
    return buildList {
      events.forEach { event ->
        when (event) {
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
                  endTime = event.at,
                  name = "Agent: ${start.agent.name}",
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
                  endTime = event.at,
                  name = "Chat: ${event.response.modelName()}",
                  model = event.response.modelName(),
                  input = LangfuseMessage.extract(start.request.messages()),
                  output = LangfuseMessage.extract(listOf(event.response.aiMessage())),
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
