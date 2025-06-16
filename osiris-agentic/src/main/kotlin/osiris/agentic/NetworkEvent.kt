package osiris.agentic

import dev.langchain4j.data.message.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import osiris.tracing.Event
import osiris.tracing.Listener

/**
 * See [runAsync].
 */
public sealed class NetworkEvent {
  public data class Event(
    val event: osiris.tracing.Event,
  ) : NetworkEvent()

  public data class Response(
    val response: List<ChatMessage>,
  ) : NetworkEvent()
}

/**
 * Instead of returning a list of [ChatMessage]s like [run] does,
 * this function returns a Kotlin [Flow] of [NetworkEvent].
 *
 * This basically treats the caller like a [Listener],
 * receiving asynchronous updates throughout the execution.
 * This is useful if intermediary updates need to be streamed, such as to the end user.
 *
 * NOTE: While the [NetworkEvent.Response] is guaranteed,
 * [NetworkEvent.Event]s are considered "best effort".
 */
public fun Network.runAsync(
  messages: List<ChatMessage>,
  listeners: List<Listener> = emptyList(),
): Flow<NetworkEvent> =
  channelFlow {
    val listener = object : Listener {
      override fun event(event: Event) {
        trySend(NetworkEvent.Event(event))
      }

      override fun flush(): Unit = Unit
    }
    val response = run(messages, listeners + listener)
    send(NetworkEvent.Response(response))
  }
