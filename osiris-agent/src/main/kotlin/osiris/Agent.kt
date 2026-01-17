package osiris

/**
 * The Osiris [Agent] interface can be used to implement any type of agent.
 * It's not strictly tied to an LLM implementation.
 *
 * For an LLM implementation, see osiris-llm.
 */
public abstract class Agent(
  public val name: String,
) {
  context(context: Context)
  public abstract suspend fun execute()
}
