package osiris.prompt

/**
 * It's usually helpful for Agents to have a shared preamble in their instructions.
 * This class supports shared preambles.
 */
public abstract class InstructionsBuilder(
  /**
   * The default instructions indicate that the agent is part of a multi-agent system,
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
  public fun build(instructions: List<Instructions>): Instructions =
    Instructions {
      combine(
        buildList {
          if (includeDefaultInstructions) add(Instructions.fromResource("osiris/prompt/default.md"))
          addAll(this@InstructionsBuilder.instructions)
          addAll(instructions)
        }.map { it.get() },
      )
    }
}

public fun InstructionsBuilder.build(instructions: Instructions): Instructions =
  build(listOf(instructions))

public fun InstructionsBuilder.fromList(buildList: MutableList<Instructions>.() -> Unit): Instructions =
  build(buildList(buildList))
