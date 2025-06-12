package osiris.agentic

public abstract class InstructionsBuilder(
  private val includeDefaultInstructions: Boolean,
) {
  protected abstract val instructions: List<Instructions>

  protected open fun combine(instructions: List<String>): String =
    instructions.joinToString(separator = "\n\n")

  public fun create(instructions: Instructions): Instructions =
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
