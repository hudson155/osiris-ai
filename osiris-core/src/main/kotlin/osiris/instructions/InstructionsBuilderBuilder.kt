package osiris.instructions

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

public class InstructionsBuilderBuilder internal constructor(
  private val includeDefaultInstructions: Boolean,
) : MutableList<Instructions> by mutableListOf() {
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
  includeDefaultInstructions: Boolean,
  block: InstructionsBuilderBuilder.() -> Unit = {},
): InstructionsBuilder =
  InstructionsBuilderBuilder(
    includeDefaultInstructions = includeDefaultInstructions,
  ).apply(block).build()
