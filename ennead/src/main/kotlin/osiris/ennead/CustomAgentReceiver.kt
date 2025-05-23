package osiris.ennead

@Suppress("UseDataClass")
public class CustomAgentReceiver<State> internal constructor(
  internal var context: AgentContext<State>,
) {
  internal var state: State
    get() = context.state
    set(value) {
      context = context.copy(state = value)
    }
}

public fun <State> AgentBuilder<State>.custom(block: CustomAgentReceiver<State>.() -> Unit) {
  implementation = AgentImplementation { context ->
    val receiver = CustomAgentReceiver(context)
    receiver.block()
    return@AgentImplementation receiver.context
  }
}

public fun <State> CustomAgentReceiver<State>.handoff(agentName: String) {
  context = context.copy(nextAgentNames = context.nextAgentNames + agentName)
}
