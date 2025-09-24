package osiris.agent

import osiris.event.AgentEvent

public abstract class Agent<C : Context>(
  public val name: String,
) {
  public suspend fun run(context: C) {
    context.send(AgentEvent.Started(name))
    execute(context)
    context.send(AgentEvent.Finished(name, success = true)) // TODO: try/catch/finally error handling.
  }

  protected abstract suspend fun execute(context: C)
}
