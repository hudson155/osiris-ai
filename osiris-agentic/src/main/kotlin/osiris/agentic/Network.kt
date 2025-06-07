package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

public abstract class Network(
  agents: List<Agent>,
) {
  protected open val entrypoint: String? = null

  internal val agents: Map<String, Agent> = agents.associateBy { it.name }

  public fun run(
    messages: List<ChatMessage>,
    entrypoint: String? = null,
  ): Flow<Event> =
    flow {
      val execution = Execution(
        network = this@Network,
        collector = this,
        messages = messages,
        entrypoint = requireNotNull(entrypoint ?: this@Network.entrypoint) { "Network must set an entrypoint." },
      )
      execution.execute()
    }
}
