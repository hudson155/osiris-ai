package osiris.langfuseTracing

import java.time.Instant
import kotlin.uuid.Uuid
import osiris.langfuseTracing.GenerationCreate.Body

internal data class GenerationCreate(
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
    val model: String,
    val input: List<LangfuseMessage>,
    val output: List<LangfuseMessage>?,
  ) : IngestionEvent.Body()
}
