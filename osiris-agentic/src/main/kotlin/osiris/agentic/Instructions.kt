package osiris.agentic

public abstract class Instructions {
  protected abstract val includeDefaultInstructions: Boolean
  protected abstract val instructions: List<String>

  protected open fun combine(instructions: List<String>): String =
    buildList {
      if (includeDefaultInstructions) {
        add(
          """
            # The system
  
            You're a part of a multi-agent system.
            You can consult other agents.
            When consulting other agents, succinctly tell them what to do or what you need.
            Don't tell them how to do their job.
          """.trimIndent(),
        )
      }
      addAll(instructions)
    }.joinToString(separator = "\n\n")

  public fun create(instructions: String): String =
    combine(this.instructions + instructions)
}
