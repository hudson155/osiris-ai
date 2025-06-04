package osiris.agentic

import osiris.core.Tool

public fun interface ToolProvider {
  public suspend fun provide(execution: Execution): Tool<*, *>
}

public fun tool(tool: Tool<*, *>): ToolProvider =
  ToolProvider { tool }
