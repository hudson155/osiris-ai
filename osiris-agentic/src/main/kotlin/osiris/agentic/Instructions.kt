package osiris.agentic

public class Instructions internal constructor(
  private val instructions: List<String>,
  private val combine: (instructions: List<String>) -> String,
) {
  public fun create(instructions: String): String =
    combine(this.instructions + instructions)
}

public class InstructionsBuilder internal constructor() : MutableList<String> by mutableListOf() {
  public var combine: (instructions: List<String>) -> String = { it.joinToString("\n\n") }

  public fun build(): Instructions =
    Instructions(
      instructions = this,
      combine = combine,
    )
}

public fun instructions(block: InstructionsBuilder.() -> Unit): Instructions =
  InstructionsBuilder().apply(block).build()
