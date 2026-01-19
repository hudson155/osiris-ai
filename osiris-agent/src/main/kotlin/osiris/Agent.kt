package osiris

import io.ktor.util.AttributeKey

/**
 * The Osiris [Agent] interface can be used to implement any type of agent.
 * It's not strictly tied to being an LLM implementation.
 *
 * For an LLM implementation, see osiris-llm.
 */
public abstract class Agent(public val name: String) {
  context(context: Context)
  public suspend fun execute() {
    context.with(Context::currentAgent, this) {
      executeAgent()
    }
  }

  context(context: Context)
  protected abstract suspend fun executeAgent()
}

private val key: AttributeKey<Agent> = AttributeKey("currentAgent")

public var Context.currentAgent: Agent?
  get() = attributes.getOrNull(key)
  set(value) {
    if (value != null) {
      attributes[key] = value
    } else {
      attributes.remove(key)
    }
  }
