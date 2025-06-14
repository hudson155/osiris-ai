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
The most basic way to consume the flow is by calling `.response()`,
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

The tool `description` is made available to the LLM.

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

TODO

### Customizing the chat request

TODO

### Custom tool executors

TODO

### Custom exit conditions

TODO
