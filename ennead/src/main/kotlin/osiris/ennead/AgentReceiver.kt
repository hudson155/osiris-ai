package osiris.ennead

public class AgentReceiver<State> internal constructor(
  internal var context: AgentContext<State>,
) {
  public var state: State
    get() = context.state
    set(value) {
      context = context.copy(state = value)
    }
}

public fun <State> AgentBuilder<State>.custom(block: suspend AgentReceiver<State>.() -> Unit) {
  implementation = AgentImplementation { context ->
    val receiver = AgentReceiver(context)
    receiver.block()
    return@AgentImplementation receiver.context
  }
}

public fun <State> AgentReceiver<State>.handoff(agentName: String) {
  val nextAgentNames = buildList {
    add(agentName)
    addAll(context.nextAgentNames)
  }
  context = context.copy(nextAgentNames = nextAgentNames)
}

public fun <State> AgentReceiver<State>.consult(agentName: String) {
  val nextAgentNames = buildList {
    add(agentName)
    add(context.currentAgentName)
    addAll(context.nextAgentNames)
  }
  context = context.copy(nextAgentNames = nextAgentNames)
}
