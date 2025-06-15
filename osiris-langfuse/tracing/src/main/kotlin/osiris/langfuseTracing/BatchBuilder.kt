package osiris.langfuseTracing

import java.time.Instant
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.uuid.Uuid
import osiris.tracing.ChatEvent
import osiris.tracing.Event
import osiris.tracing.LlmEvent
import osiris.tracing.ToolEvent

internal class BatchBuilder {
  val ingestionEvents: Queue<IngestionEvent<*>> = ConcurrentLinkedQueue()

  fun chatEvent(event: Event) {
    val start = event.start
    val end = event.end as Event.End
    val startDetails = start.details as ChatEvent.Start
    val endDetails = end.details as ChatEvent.End
    ingestionEvents += GenerationCreate(
      id = Uuid.random(),
      timestamp = Instant.now(),
      body = GenerationCreate.Body(
        id = event.spanId,
        traceId = event.rootSpanId,
        parentObservationId = event.parentSpanId,
        startTime = start.at,
        endTime = end.at,
        name = "Chat: ${endDetails.response.modelName()}",
        model = endDetails.response.modelName(),
        input = LangfuseMessage.extract(startDetails.request.messages()),
        output = LangfuseMessage.extract(listOf(endDetails.response.aiMessage())),
      ),
    )
  }

  fun llmEvent(event: Event) {
    val start = event.start
    val end = event.end as Event.End
    val startDetails = start.details as LlmEvent.Start
    val endDetails = end.details as LlmEvent.End
    ingestionEvents += TraceCreate(
      id = Uuid.random(),
      timestamp = Instant.now(),
      body = TraceCreate.Body(
        id = event.spanId,
        timestamp = start.at,
        name = "Osiris",
        input = deriveText(startDetails.input),
        output = deriveText(endDetails.output),
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
        output = endDetails.executionResult.text(),
      ),
    )
  }

  @Suppress("LongMethod")
  fun build(): BatchIngestion =
    BatchIngestion(
      batch = ingestionEvents.toList(),
    )
}
