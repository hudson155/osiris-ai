package osiris.langfuseTracing

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant
import kotlin.uuid.Uuid
import osiris.langfuseTracing.IngestionEvent.Body

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(GenerationCreate::class, name = "generation-create"),
  JsonSubTypes.Type(SpanCreate::class, name = "span-create"),
  JsonSubTypes.Type(TraceCreate::class, name = "trace-create"),
)
internal sealed class IngestionEvent<T : Body> {
  abstract val id: Uuid
  abstract val timestamp: Instant
  abstract val body: T

  internal sealed class Body {
    abstract val id: Uuid
  }
}
