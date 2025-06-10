package osiris.langfuseTracing

import java.time.Instant
import kotlin.uuid.Uuid
import osiris.langfuseTracing.TraceCreate.Body

internal data class TraceCreate(
  override val id: Uuid,
  override val timestamp: Instant,
  override val body: Body,
) : IngestionEvent<Body>() {
  internal data class Body(
    override val id: Uuid,
    val timestamp: Instant,
    val input: String?,
    val output: String?,
  ) : IngestionEvent.Body()
}
