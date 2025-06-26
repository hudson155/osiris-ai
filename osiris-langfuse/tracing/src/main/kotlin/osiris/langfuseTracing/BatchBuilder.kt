package osiris.langfuseTracing

import java.time.Instant
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.uuid.Uuid
import osiris.tracing.AgentEvent
import osiris.tracing.ChatEvent
import osiris.tracing.Event
import osiris.tracing.ToolEvent
import osiris.tracing.TraceEvent

@Suppress("ForbiddenMethodCall")
internal class BatchBuilder {
  val ingestionEvents: Queue<IngestionEvent<*>> = ConcurrentLinkedQueue()

  fun agentEvent(event: Event) {
    val start = event.start
    val end = event.end as Event.End
    val startDetails = start.details as AgentEvent.Start
    val endDetails = end.details as AgentEvent.End
    ingestionEvents += SpanCreate(
      id = Uuid.random(),
      timestamp = Instant.now(),
      body = SpanCreate.Body(
        id = event.spanId,
        traceId = event.rootSpanId,
        parentObservationId = event.parentSpanId,
        startTime = start.at,
        endTime = end.at,
        name = "Agent: ${startDetails.agent.name}",
        input = startDetails.input,
        output = endDetails.output,
      ),
    )
  }

  fun chatEvent(event: Event) {
    val start = event.start
    val end = event.end as Event.End
    val startDetails = start.details as ChatEvent.Start
    val endDetails = end.details as ChatEvent.End
    val modelName = endDetails.response?.modelName() ?: startDetails.request.modelName()
    val input = LangfuseMessage.extract(startDetails.request.messages())
    val output = endDetails.response?.let { LangfuseMessage.extract(listOf(it.aiMessage())) }
    ingestionEvents += GenerationCreate(
      id = Uuid.random(),
      timestamp = Instant.now(),
      body = GenerationCreate.Body(
        id = event.spanId,
        traceId = event.rootSpanId,
        parentObservationId = event.parentSpanId,
        startTime = start.at,
        endTime = end.at,
        name = "Chat: $modelName",
        model = modelName,
        input = input,
        output = output,
      ),
    )
  }

  fun toolEvent(event: Event) {
    val start = event.start
    val end = event.end as Event.End
    val startDetails = start.details as ToolEvent.Start
    val endDetails = end.details as ToolEvent.End
    ingestionEvents += SpanCreate(
      id = Uuid.random(),
      timestamp = Instant.now(),
      body = SpanCreate.Body(
        id = event.spanId,
        traceId = event.rootSpanId,
        parentObservationId = event.parentSpanId,
        startTime = start.at,
        endTime = end.at,
        name = "Tool: ${startDetails.tool.name}",
        input = startDetails.executionRequest.arguments(),
        output = endDetails.executionResult?.text(),
      ),
    )
  }

  fun traceEvent(event: Event) {
    val start = event.start
    val end = event.end as Event.End
    val startDetails = start.details as TraceEvent.Start
    val endDetails = end.details as TraceEvent.End
    ingestionEvents += TraceCreate(
      id = Uuid.random(),
      timestamp = Instant.now(),
      body = TraceCreate.Body(
        id = event.spanId,
        timestamp = start.at,
        name = startDetails.name,
        input = startDetails.input,
        output = endDetails.output,
      ),
    )
  }

  fun build(): BatchIngestion =
    BatchIngestion(
      batch = ingestionEvents.toList(),
    )
}
