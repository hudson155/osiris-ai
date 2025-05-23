package osiris.ennead

public fun interface AgentImplementation<State> {
  public fun execute(state: State): AgentResult<State>
}
