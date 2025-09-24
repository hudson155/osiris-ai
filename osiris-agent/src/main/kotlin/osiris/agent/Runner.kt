package osiris.agent

public abstract class Runner {
  public suspend fun <C : Context> run(agent: Agent<C>, context: C) {
    agent.run(context)
  }
}
