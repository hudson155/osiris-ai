package osiris.event

import java.time.Instant
import osiris.core.Tool

public sealed class ToolEvent : Event() {
  public data class Start(
    override val at: Instant,
    val tool: Tool<*, *>,
    val id: String,
    val input: String,
  ) : ToolEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(
      tool: Tool<*, *>,
      id: String,
      input: String,
    ) : this(
      at = Instant.now(),
      tool = tool,
      id = id,
      input = input,
    )
  }

  public data class End(
    override val at: Instant,
    val output: String,
  ) : ToolEvent() {
    @Suppress("ForbiddenMethodCall")
    public constructor(output: String) : this(
      at = Instant.now(),
      output = output,
    )
  }
}
