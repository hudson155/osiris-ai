package osiris.agent

public abstract class Agent<C : Context>(
  public val name: String,
) {
  public abstract suspend fun run(context: C)
}
