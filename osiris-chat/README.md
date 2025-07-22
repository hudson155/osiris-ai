# Osiris Chat

Osiris's **chat module** supports basic Kotlin-idiomatic LLM interaction.
See the [**agentic module**](../osiris-agentic) if you're building an agentic framework.

## Installation

`software.airborne.osiris:osiris-chat:0.28.2`

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
  implementation("software.airborne.osiris:osiris-chat:0.28.2")
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

val response = llm(model, messages)

response.convert<String>()
// 2 + 2 equals 4.
```

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
val modelFactory =
  modelFactory {
    openAiApiKey = ProtectedString("...")
  }

val model = modelFactory.openAi("gpt-4.1-nano")
```

Alternatively, you can instantiate a model with Langchain4j directly.

```kotlin
val model = OpenAiChatModel.builder().apply {
  modelName("gpt-4.1-nano")
  apiKey("...")
  strictJsonSchema(true)
  strictTools(true)
}.build()
```

### Make a request

Make your request by calling `llm()`.

```kotlin
val response = llm(
  model = model,
  messages = listOf(
    UserMessage("What's 2+2?"),
  ),
)
```

Then convert the response to the appropriate type using `.convert()`.

```kotlin
response.convert<String>()
// 2 + 2 equals 4.
```

### Using Tools

To make Tools available to the LLM,
extend the `Tool` class.

```kotlin
class WeatherTool : Tool<WeatherTool.Input>("weather") {
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

The Tool name and description are made available to the LLM.

The input schema should conform to [osiris-schema](../osiris-schema).
Optionally use `@LlmSchema.Description` to provide additional context to the LLM.

Now that you've created your Tool,
you can make it available when you call `llm()`.

```kotlin
val response = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
  tools = listOf(WeatherTool()),
)

response.convert<String>()
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

val response = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
    SystemMessage("Provide a JSON representation of the person matching this description."),
  ),
  responseType = kairoType<Person>(),
)

response.convert<Person>()
// Person(name=Jeff Hudson, age=29)
```

## Advanced usage

### Customizing the chat request

If you need to customize the Langchain4j chat request,
pass a custom `chatRequestBlock` to `llm()`.

```kotlin
val response = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
  tools = listOf(WeatherTool()),
  chatRequestBlock = {
    maxOutputTokens(1000)
    toolChoice(ToolChoice.AUTO)
    // Langchain4j offers many other options.
  },
)

response.convert<String>()
// The weather in Calgary is sunny with a temperature of 15 degrees Celsius.
```

### Custom Tool executors

By default, Tools are executed in parallel.

Alternatively, you can choose to run them on a specific coroutine dispatcher.

```kotlin
val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

val response = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
  tools = listOf(WeatherTool()),
  toolExecutor = ToolExecutor.Dispatcher(dispatcher),
)

response.convert<String>()
// The weather in Calgary is sunny with a temperature of 15 degrees Celsius.
```

You can run Tools sequentially if you need to.

```kotlin
val response = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
  tools = listOf(WeatherTool()),
  toolExecutor = ToolExecutor.Sequential(),
)

response.convert<String>()
// The weather in Calgary is sunny with a temperature of 15 degrees Celsius.
```

Or you can create your own custom `ToolExecutor`.

### Custom exit conditions

By default, Osiris will run LLM requests in a loop,
executing Tool calls until the LLM responds.
This means several round trips to the LLM.

If you want to exit at a different point,
implement a custom `LlmExitCondition`.

### Tracing

You can add tracing by creating a Tracer and passing it to `llm()`.

```kotlin
val tracer = tracer {
  listener(langfuse.trace())
}

llm(
  model = model,
  messages = listOf(
    UserMessage("What's 2+2?"),
  ),
  tracer = tracer,
)
```

For more details, see [osiris-tracing](../osiris-tracing).
