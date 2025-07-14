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
public sealed class IngestionEvent<T : Body> {
  public abstract val id: Uuid
  public abstract val timestamp: Instant
  public abstract val body: T

  public sealed class Body {
    public abstract val id: Uuid
  }
}
