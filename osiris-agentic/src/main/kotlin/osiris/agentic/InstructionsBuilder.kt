package osiris.agentic

internal class InstructionsImpl(
  override val includeDefaultInstructions: Boolean,
  override val instructions: List<String>,
  private val combineBlock: ((instructions: List<String>) -> String)?,
) : Instructions() {
  override fun combine(instructions: List<String>): String {
    combineBlock ?: return super.combine(instructions)
    return combineBlock(instructions)
  }
}

public class InstructionsBuilder internal constructor(
  private val includeDefaultInstructions: Boolean,
) : MutableList<String> by mutableListOf() {
  public var combine: (instructions: List<String>) -> String = { it.joinToString("\n\n") }

  internal fun build(): Instructions =
    InstructionsImpl(
      includeDefaultInstructions = includeDefaultInstructions,
      instructions = this,
      combineBlock = combine,
    )
}

public fun instructions(
  includeDefaultInstructions: Boolean,
  block: InstructionsBuilder.() -> Unit = {},
): Instructions =
  InstructionsBuilder(
    includeDefaultInstructions = includeDefaultInstructions,
  ).apply(block).build()
