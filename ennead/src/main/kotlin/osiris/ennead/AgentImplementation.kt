package osiris.ennead

public fun interface AgentImplementation<State> {
  public fun execute(context: AgentContext<State>): AgentContext<State>
}
