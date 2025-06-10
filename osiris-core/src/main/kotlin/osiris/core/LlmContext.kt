package osiris.core

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.channels.ProducerScope
import osiris.event.Event

public class LlmContext(
  producer: ProducerScope<Event>,
) : AbstractCoroutineContextElement(key), ProducerScope<Event> by producer {
  public companion object {
    public val key: CoroutineContext.Key<LlmContext> =
      object : CoroutineContext.Key<LlmContext> {}
  }
}

public suspend fun getLlmContext(): LlmContext =
  checkNotNull(coroutineContext[LlmContext.key])
