package osiris.langfuseTracing

import java.time.Instant
import kairo.serialization.util.kairoWrite
import kotlin.uuid.Uuid
import osiris.core.llmMapper
import osiris.span.AgentEvent
import osiris.span.ChatEvent
import osiris.span.ExecutionEvent
import osiris.span.Span
import osiris.span.ToolEvent

internal class BatchBuilder(
  private val trace: List<Span<*>>,
) {
  private val traceId: Uuid = Uuid.random()

  @Suppress("ForbiddenMethodCall")
  private val now: Instant = Instant.now()

  @Suppress("LongMethod")
  fun build(): BatchIngestion =
    BatchIngestion(
      batch = trace.mapNotNull { span ->
        when (val details = span.details) {
          is ExecutionEvent ->
            TraceCreate(
              id = Uuid.random(),
              timestamp = now,
              body = TraceCreate.Body(
                id = traceId,
                timestamp = span.start,
                name = "Trace: ${details.network.name}",
                input = details.input,
                output = details.output,
              ),
            )
          is AgentEvent ->
            SpanCreate(
              id = Uuid.random(),
              timestamp = now,
              body = SpanCreate.Body(
                id = Uuid.random(), // TODO: Parent.
                traceId = traceId,
                parentObservationId = null, // TODO: Wrong.
                startTime = span.start,
                endTime = span.end,
                name = "Agent: ${details.agent.name}",
                input = details.input,
                output = details.output,
              ),
            )
          is ChatEvent ->
            GenerationCreate(
              id = Uuid.random(),
              timestamp = now,
              body = GenerationCreate.Body(
                id = Uuid.random(), // TODO: Parent.
                traceId = traceId,
                parentObservationId = null, // TODO: Wrong.
                startTime = span.start,
                endTime = span.end,
                name = "Chat: ${details.response.modelName()}",
                model = details.response.modelName(),
                input = LangfuseMessage.extract(details.request.messages()),
                output = LangfuseMessage.extract(listOf(details.response.aiMessage())),
              ),
            )
          is ToolEvent<*, *> ->
            SpanCreate(
              id = Uuid.random(),
              timestamp = now,
              body = SpanCreate.Body(
                id = Uuid.random(), // TODO: Parent.
                traceId = traceId,
                parentObservationId = null, // TODO: Wrong.
                startTime = span.start,
                endTime = span.end,
                name = "Tool: ${details.tool.name}",
                input = llmMapper.kairoWrite(details.input),
                output = llmMapper.kairoWrite(details.output),
              ),
            )
          else -> null
        }
      },
    )
}
