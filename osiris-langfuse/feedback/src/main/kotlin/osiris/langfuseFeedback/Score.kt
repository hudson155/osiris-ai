package osiris.langfuseFeedback

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlin.uuid.Uuid

public object Score {
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dataType")
  @JsonSubTypes(
    JsonSubTypes.Type(Creator.BooleanScore::class, name = "BOOLEAN"),
    JsonSubTypes.Type(Creator.BooleanScore::class, name = "CATEGORICAL"),
    JsonSubTypes.Type(Creator.BooleanScore::class, name = "NUMERIC"),
  )
  public sealed class Creator<T : Any> {
    public abstract val sessionId: String?
    public abstract val traceId: Uuid?
    public abstract val name: String
    public abstract val value: T
    public abstract val comment: String?

    public data class BooleanScore(
      override val sessionId: String?,
      override val traceId: Uuid?,
      override val name: String,
      @JsonIgnore
      override val value: Boolean,
      override val comment: String?,
    ) : Creator<Boolean>() {
      @JsonProperty("value")
      val formattedValue: Long = if (value) 1 else 0
    }

    public data class CategoricalScore(
      override val sessionId: String?,
      override val traceId: Uuid?,
      override val name: String,
      override val value: String,
      override val comment: String?,
    ) : Creator<String>()

    public data class NumericScore(
      override val sessionId: String?,
      override val traceId: Uuid?,
      override val name: String,
      override val value: Long,
      override val comment: String?,
    ) : Creator<Long>()
  }
}
