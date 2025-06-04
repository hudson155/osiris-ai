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

public fun instructions(
  includeDefaultInstructions: Boolean,
  block: InstructionsBuilder.() -> Unit = {},
): Instructions =
  InstructionsBuilder().apply {
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
    block()
  }.build()
