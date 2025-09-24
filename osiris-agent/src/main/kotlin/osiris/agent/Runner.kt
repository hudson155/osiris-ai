package osiris.agent

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import osiris.event.Event

public abstract class Runner {
  public suspend fun <C : Context> run(agent: Agent<C>, context: C) {
    coroutineScope {
      launch {
        for (event in context) {
          consume(event)
        }
      }
      try {
        agent.run(context)
      } finally {
        context.close()
      }
    }
  }

  protected abstract suspend fun consume(event: Event)
}
