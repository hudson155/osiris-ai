package osiris.langfuseTracing

import java.time.Instant
import kotlin.uuid.Uuid
import osiris.langfuseTracing.SpanCreate.Body

internal data class SpanCreate(
  override val id: Uuid,
  override val timestamp: Instant,
  override val body: Body,
) : IngestionEvent<Body>() {
  internal data class Body(
    override val id: Uuid,
    val traceId: Uuid,
    val parentObservationId: Uuid?,
    val startTime: Instant,
    val endTime: Instant,
    val name: String,
    val input: String?,
    val output: String?,
  ) : IngestionEvent.Body()
}
