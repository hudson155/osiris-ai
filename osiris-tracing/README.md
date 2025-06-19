# Osiris Tracing

Osiris's **tracing module** lets you add arbitrary listeners to LLM requests and agentic executions,
as well as pipe those listeners to tracing tools such as Langfuse.

For Langfuse-specific tracing, see [osiris-langfuse-tracing](../osiris-langfuse/tracing).

## Installation

Included by default with both [osiris-chat](../osiris-chat) and [osiris-agentic](../osiris-agentic).

## Events

- `TraceEvent`:
  The outermost span.
  Both the chat module and the agentic framework will have this as the top level span.
  You can access the string input and output.
- `AgentEvent`:
  If using the agentic framework,
  each Agent's turn will have a span.
  You can access the Agent itself,
  as well as the string input and output.
- `ChatEvent`:
  Each separate request to the LLM will have a span.
  You can access the Langchain4j chat request and Langchain4j chat response.
- `ToolEvent`:
  Each tool call will have a span.
  You can access the Tool,
  Langchain4j execution request, and Langchain4j execution result.

[Custom events](#custom-events) are also supported.

## Usage

### Chat module

If you're using the chat module,
you can add tracing by creating a Tracer and passing it to `llm()`.

```kotlin
val tracer = tracer {
  listener(EventLogger) // Logs all events.
  listener(MyListener()) // Or create your own listener.
}

llm(
  model = model,
  messages = listOf(
    UserMessage("What's 2+2?"),
  ),
  tracer = tracer,
)
```

If you don't pass a Tracer, tracing is disabled.

### Agentic framework

If you're using the agentic framework,
you can add tracing by adding Listeners to the Network

```kotlin
val network =
  network("network") {
    entrypoint = ecommerceChatbot.name
    agents += ecommerceChatbot
    agents += ecommerceOrderTracker
    listener(EventLogger) // Logs all events.
    listener(MyListener()) // Or create your own listener.
  }
```

To dynamically add Listeners at Network execution time,
pass them to `Network.run()`.

```kotlin
network.run(messages, listeners = listOf(EventLogger, MyListener()))
```

If you don't include any listeners, tracing is disabled.

### Custom Listeners

The examples above use `EventLogger`,
which is a built-in Listener that logs all events.

To create your own custom Listener, implement `Listener`.

```kotlin
class MyListener : Listener {
  override fun event(event: Event) {
    TODO("Your event implementation.")
  }

  override fun flush() {
    TODO("Your flush implementation.")
  }
}
```

## Advanced usage

### Custom events

In addition to the built-in event types, you can also create your own event types.

First, define your custom event type.
It must have both a `Start` and an `End` data class.
The properties can be whatever you want to make available.

```kotlin
object MyEvent {
  data class Start(
    val myStartProperty: String,
  ) : Event.Details()

  data class End(
    val myEndProperty: String,
  ) : Event.Details()
}
```

Then, trace your operation.

```kotlin
trace({ MyEvent.Start("start") }, { MyEvent.End(it) }) {
  TODO("Perform your operation.")
  return@trace "end"
}
```

Note: Custom events are not yet supported by [osiris-langfuse-tracing](../osiris-langfuse/tracing)
