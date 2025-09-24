package osiris.event

import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder

@Serializable
public abstract class Event {
  public companion object {
    public fun PolymorphicModuleBuilder<Event>.defaultSubclasses() {
      subclass(AgentEvent.Started::class, AgentEvent.Started.serializer())
      subclass(AgentEvent.Finished::class, AgentEvent.Finished.serializer())
      subclass(MessageEvent.Ai::class, MessageEvent.Ai.serializer())
      subclass(MessageEvent.User::class, MessageEvent.User.serializer())
    }
  }
}
