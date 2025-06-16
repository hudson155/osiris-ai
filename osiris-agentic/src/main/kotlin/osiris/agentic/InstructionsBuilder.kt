package osiris.agentic

/**
 * It's usually helpful for Agents to have a shared preamble in their instructions.
 * This class supports shared preambles.
 */
public abstract class InstructionsBuilder(
  /**
   * The default instructions indicate that the Agent is part of a multi-agent system,
   * indicating some basic best practices around that.
   */
  private val includeDefaultInstructions: Boolean,
) {
  /**
   * These shared instructions are added after the default instructions,
   * but before Agent-specific instructions.
   */
  protected abstract val instructions: List<Instructions>

  /**
   * Instructions are typically joined by 2 newline characters,
   * but this can be customized.
   */
  protected open fun combine(instructions: List<String>): String =
    instructions.joinToString(separator = "\n\n")

  /**
   * Create the instructions for an Agent,
   * using this builder's configuration.
   */
  public fun build(instructions: Instructions): Instructions =
    Instructions {
      combine(
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
          this@InstructionsBuilder.instructions.forEach { add(it.get()) }
          add(instructions.get())
        },
      )
    }
}
