package osiris.agentic

internal class InstructionsBuilderImpl(
  includeDefaultInstructions: Boolean,
  override val instructions: List<Instructions>,
  private val combineBlock: ((instructions: List<String>) -> String)?,
) : InstructionsBuilder(
  includeDefaultInstructions = includeDefaultInstructions,
) {
  override fun combine(instructions: List<String>): String {
    combineBlock ?: return super.combine(instructions)
    return combineBlock(instructions)
  }
}

/**
 * These shared instructions are added after the default instructions,
 * but before Agent-specific instructions.
 */
public class InstructionsBuilderBuilder internal constructor(
  private val includeDefaultInstructions: Boolean,
) : MutableList<Instructions> by mutableListOf() {
  /**
   * Instructions are typically joined by 2 newline characters,
   * but this can be customized.
   */
  public var combine: ((instructions: List<String>) -> String)? = null

  internal fun build(): InstructionsBuilder =
    InstructionsBuilderImpl(
      includeDefaultInstructions = includeDefaultInstructions,
      instructions = this,
      combineBlock = combine,
    )
}

/**
 * Helper DSL to build an [InstructionsBuilder].
 */
public fun instructionsBuilder(
  /**
   * The default instructions indicate that the Agent is part of a multi-agent system,
   * indicating some basic best practices around that.
   */
  includeDefaultInstructions: Boolean,

  block: InstructionsBuilderBuilder.() -> Unit = {},
): InstructionsBuilder =
  InstructionsBuilderBuilder(
    includeDefaultInstructions = includeDefaultInstructions,
  ).apply(block).build()
