package osiris.langfuseTracing

import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import java.time.Instant
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.uuid.Uuid
import osiris.tracing.Event

@Suppress("ForbiddenMethodCall")
internal class BatchBuilder {
  private var traceId: Uuid? = null
  private val ingestionEvents: Queue<IngestionEvent> = ConcurrentLinkedQueue()

  fun event(event: Event) {
    if (event !is Event.End) return
    var type: String
    val body = mutableMapOf<String, Any?>().apply {
      put("id", event.spanId)
      put("name", event.type) // TODO: Improve.
      put("input", event.startContent)
      put("output", event.endContent)
      put("level", event.level)
      if (event.type == "Trace") {
        type = "trace-create"
        put("timestamp", event.startAt)
        put("userId", null)
        put("sessionId", null)
        put("metadata", emptyMap<String, Any>())
      } else {
        type = "span-create"
        put("traceId", event.rootSpanId)
        put("parentObservationId", event.parentSpanId)
        put("startTime", event.startAt)
        put("endTime", event.endAt)
      }
    }
    when (event.type) {
      "Chat" -> {
        type = "generation-create"
        body["model"] =
          (event.endProperties["response"] as ChatResponse).modelName()
            ?: (event.startProperties["request"] as ChatRequest).modelName()
      }
      "Trace" -> {
        traceId = event.rootSpanId
      }
    }
    ingestionEvents += IngestionEvent(
      id = Uuid.random(),
      timestamp = Instant.now(),
      type = type,
      body = body,
    )
  }

  fun build(): Pair<Uuid, BatchIngestion>? =
    traceId?.let { traceId ->
      val batchIngestion = BatchIngestion(
        batch = ingestionEvents.toList(),
      )
      return@let Pair(traceId, batchIngestion)
    }
}
