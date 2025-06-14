# Osiris Core

Osiris's **core module** supports basic Kotlin-idiomatic LLM interaction.
See the [**agentic module**](../osiris-agentic) if you're building an agentic framework.

## Installation

`software.airborne.osiris:osiris-core:0.13.0`

<details>

<summary>Gradle</summary>

```kotlin
plugins {
  id("com.google.cloud.artifactregistry.gradle-plugin")
}

repositories {
  maven {
    url = uri("artifactregistry://us-central1-maven.pkg.dev/airborne-software/maven")
  }
}

dependencies {
  implementation("software.airborne.osiris:osiris-core:0.13.0")
}
```

</details>

## Usage

`llm()` is the primary entrypoint.

```kotlin
val model = modelFactory.openAi("gpt-4.1-nano")
val messages = listOf(
  UserMessage("What's 2+2?"),
)

val flow = llm(model, messages)

flow.response().convert<String>()
// 2 + 2 equals 4.
```

#### Flows

Osiris uses Kotlin Flows to provide asynchronous responses with incremental updates.
The most basic way to consume the Flow is by calling `.response()`,
which disregards everything except the final chat message.
Refer to the [Events section](#events) for advanced Flow usage.

#### Langchain4j

Osiris is built atop [Langchain4j](https://github.com/langchain4j/langchain4j).
Some of the classes you'll use with Osiris
(`ChatModel`, `ChatMessage`, `ChatRequest`, `ChatResponse`, etc.)
are actually from Langchain4j.

### Instantiate a model

First, instantiate a Langchain4j model.
We recommend using `ModelFactory`,
an optional way to instantiate model instances.
To use it, add [osiris-open-ai](../osiris-open-ai) or another LLM provider.

```kotlin
val modelFactory: ModelFactory =
  modelFactory {
    openAiApiKey = ProtectedString("...")
  }

val model = modelFactory.openAi("gpt-4.1-nano")
```

You can also instantiate a model with Langchain4j directly.

```kotlin
val model = OpenAiChatModel.builder().apply {
  modelName("gpt-4.1-nano")
  apiKey("...")
  strictJsonSchema(true)
  strictTools(true)
  block()
}.build()
```

### Make a request

Make your request by calling `llm()`.

```kotlin
val flow = llm(
  model = model,
  messages = listOf(
    ChatMessage("What's 2+2?"),
  ),
)
```

Then collect the Flow by calling `.response()`,
and convert it to the appropriate type using `.convert()`.

```kotlin
flow.response().convert<String>()
// 2 + 2 equals 4.
```

### Using tools

To make tools available to the LLM,
extend the `SimpleTool` class.

```kotlin
class WeatherTool : SimpleTool<WeatherTool.Input>("weather") {
  data class Input(
    @LlmSchema.Description("The city to get the weather for.")
    val location: String,
  )

  override val description: LazySupplier<String> =
    LazySupplier { "Gets the weather." }

  override suspend fun execute(input: Input): String =
    TODO("Your implementation.")
}
```

The tool name and description are made available to the LLM.

The input schema should conform to [osiris-schema](../osiris-schema).
Optionally use `@LlmSchema.Description` to provide additional context to the LLM.

Now that you've created your tool,
you can make it available when you call `llm()`.

```kotlin
val flow = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
  tools = listOf(WeatherTool()),
)

flow.response().convert<String>()
// The weather in Calgary is sunny with a temperature of 15 degrees Celsius.
```

### Structured output

Structured output is supported through [osiris-schema](../osiris-schema).

```kotlin
@LlmSchema.SchemaName("person")
data class Person(
  val name: String,
  val age: Int,
)

val flow = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
    SystemMessage("Provide a JSON representation of the person matching this description."),
  ),
  responseType = Person::class,
)

flow.response().convert<Person>()
// Person(name=Jeff Hudson, age=29)
```

## Advanced usage

### Events

Osiris uses Kotlin Flows to provide asynchronous responses with incremental updates.
Instead of calling `.response()` on the Flow
(which disregards everything except the final chat message),
you can listen for events instead.

```kotlin
val flow = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
  tools = listOf(WeatherTool()),
)

flow.collect { println(it) }
// MessageEvent(message=AiMessage { toolExecutionRequests = [...] })
// MessageEvent(message=ToolExecutionResultMessage { ... })
// MessageEvent(message=AiMessage { text = "..." })
```

When doing this, notice that the events are not printed all at once.
Instead, they're printed across a second or two.

You can also use other Kotlin Flow operations.

### Customizing the chat request

If you need to customize the Langchain4j chat request,
pass a custom `chatRequestBlock` to `llm()`.

```kotlin
val flow = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's 2+2?"),
  ),
  chatRequestBlock = {
    maxOutputTokens(100)
    toolChoice(ToolChoice.AUTO)
    // Langchain4j offers many other options.
  },
)

flow.response().convert<String>()
// 2 + 2 equals 4.
```

### Custom tool executors

By default, tools are executed in parallel on Kotlin's `Dispatchers.IO` coroutine dispatcher.

Alternatively, you can choose to run them on a different coroutine dispatcher.

```kotlin
val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

val flow = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
  tools = listOf(WeatherTool()),
  toolExecutor = ToolExecutor.Dispatcher(dispatcher),
)

flow.response().convert<String>()
// The weather in Calgary is sunny with a temperature of 15 degrees Celsius.
```

You can run tools sequentially if you need to.

```kotlin
val flow = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
  tools = listOf(WeatherTool()),
  toolExecutor = ToolExecutor.Sequential(),
)

flow.response().convert<String>()
// The weather in Calgary is sunny with a temperature of 15 degrees Celsius.
```

Or you can create your own custom `ToolExecutor`.

### Custom exit conditions

By default, Osiris will run LLM requests in a loop,
executing tool calls until the LLM responds.
This means several round trips to the LLM.

If you want to exit at a different point,
implement a custom `ExitCondition`.

```kotlin
val flow = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
  tools = listOf(WeatherTool()),
  exitCondition = ExitCondition { response ->
    response.isNotEmpty() // Exit after 1 turn.
  },
)

flow.collect { println(it) }
// MessageEvent(message=AiMessage { toolExecutionRequests = [...] })
```
