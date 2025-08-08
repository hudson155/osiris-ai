package osiris.langfuseTracing

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import java.time.Instant
import kotlin.uuid.Uuid
import osiris.tracing.TraceLevel

public data class IngestionEvent(
  val id: Uuid,
  val type: String,
  val timestamp: Instant,
  val body: Map<String, Any?>,
) {
  public data class Body(
    val id: Uuid,
    val traceId: Uuid,
    val parentObservationId: Uuid?,
    val startTime: Instant,
    val endTime: Instant,
    val name: String,
    val input: Any?,
    val output: Any?,
    val level: TraceLevel,
    @field:JsonAnyGetter
    @param:JsonAnySetter
    val properties: Map<String, Any?> = emptyMap(),
  )
}
