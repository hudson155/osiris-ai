package osiris.tracing

import java.time.Instant
import kotlin.uuid.Uuid

public abstract class Event {
  public abstract val spanId: Uuid
  public abstract val parentSpanId: Uuid?
  public abstract val rootSpanId: Uuid

  public data class Start(
    override val spanId: Uuid,
    override val parentSpanId: Uuid?,
    override val rootSpanId: Uuid,
    val at: Instant,
    val type: String,
    val name: String,
    val content: Any?,
    val properties: Map<String, Any?>,
  ) : Event() {
    public data class Creator(
      val name: String,
      val type: String,
      val content: Any?,
      val properties: Map<String, Any?> = emptyMap(),
    )

    public companion object
  }

  public data class End(
    override val spanId: Uuid,
    override val parentSpanId: Uuid?,
    override val rootSpanId: Uuid,
    val startAt: Instant,
    val endAt: Instant,
    val type: String,
    val name: String,
    val level: TraceLevel,
    val startContent: Any?,
    val endContent: Any?,
    val startProperties: Map<String, Any?>,
    val endProperties: Map<String, Any?>,
  ) : Event() {
    public data class Creator(
      val name: String? = null,
      val content: Any?,
      val properties: Map<String, Any?> = emptyMap(),
    )

    public companion object
  }
}

internal fun Event.Start.Companion.create(
  spanId: Uuid,
  parentSpanId: Uuid?,
  rootSpanId: Uuid,
  creator: Event.Start.Creator,
): Event.Start =
  Event.Start(
    spanId = spanId,
    parentSpanId = parentSpanId,
    rootSpanId = rootSpanId,
    at = Instant.now(),
    type = creator.type,
    name = creator.name,
    content = creator.content,
    properties = creator.properties,
  )

internal fun Event.End.Companion.create(
  start: Event.Start,
  tracer: Tracer,
  creator: Event.End.Creator,
): Event.End =
  Event.End(
    spanId = start.spanId,
    parentSpanId = start.parentSpanId,
    rootSpanId = start.rootSpanId,
    startAt = start.at,
    endAt = Instant.now(),
    type = start.type,
    name = creator.name ?: start.name,
    level = tracer.level,
    startContent = start.content,
    endContent = creator.content,
    startProperties = start.properties,
    endProperties = creator.properties,
  )
