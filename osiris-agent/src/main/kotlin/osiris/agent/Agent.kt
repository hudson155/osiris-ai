package osiris.agent

public abstract class Agent(
  public val name: String,
) {
  public abstract suspend fun run(context: Context)
}
