package osiris.ennead

public class CustomAgentReceiver<State> internal constructor(
  public var state: State,
)

public fun <State> AgentBuilder<State>.custom(block: CustomAgentReceiver<State>.() -> Unit) {
  implementation = AgentImplementation { state ->
    val receiver = CustomAgentReceiver(state)
    receiver.block()
    return@AgentImplementation AgentResult(receiver.state)
  }
}
