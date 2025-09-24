package osiris.agent

public abstract class Agent<C : Context>(
  public val name: String,
) {
  public suspend fun run(context: C) {
    execute(context)
  }

  protected abstract suspend fun execute(context: C)
}
