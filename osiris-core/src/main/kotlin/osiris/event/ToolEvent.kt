package osiris.event

import osiris.core.Tool

public sealed class ToolEvent : Event() {
  public abstract val tool: Tool<*, *>
  public abstract val id: String
  public abstract val input: String

  public data class Start(
    override val tool: Tool<*, *>,
    override val id: String,
    override val input: String,
  ) : ToolEvent()

  public data class End(
    override val tool: Tool<*, *>,
    override val id: String,
    override val input: String,
    val output: String,
  ) : ToolEvent()
}
