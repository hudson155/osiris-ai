package osiris.langfuseTracing

import java.time.Instant
import kotlin.uuid.Uuid

public data class IngestionEvent(
  val id: Uuid,
  val type: String,
  val timestamp: Instant,
  val body: Map<String, Any?>,
)
