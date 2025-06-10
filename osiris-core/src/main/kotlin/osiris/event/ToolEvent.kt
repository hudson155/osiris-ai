package osiris.event

import osiris.core.Tool

public sealed class ToolEvent {
  public abstract val tool: Tool<*, *>
  public abstract val id: String
  public abstract val input: String

  public data class Start(
    val tool: Tool<*, *>,
    val id: String,
    val input: String,
  ) : Event()

  public data class End(
    val tool: Tool<*, *>,
    val id: String,
    val input: String,
    val output: String,
  ) : Event()
}
