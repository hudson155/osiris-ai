package osiris.langfuseTracing

import java.time.Instant
import kotlin.uuid.Uuid
import osiris.langfuseTracing.TraceCreate.Body

public data class TraceCreate(
  override val id: Uuid,
  override val timestamp: Instant,
  override val body: Body,
) : IngestionEvent<Body>() {
  public data class Body(
    override val id: Uuid,
    val timestamp: Instant,
    val name: String,
    val userId: String?,
    val sessionId: String?,
    val metadata: Map<String, Any>,
    val input: String?,
    val output: String?,
  ) : IngestionEvent.Body()
}
