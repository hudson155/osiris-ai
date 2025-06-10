# Osiris AI

> [Osiris](https://en.wikipedia.org/wiki/Osiris)
> was the god of fertility, agriculture, the afterlife, the dead, resurrection, life, and vegetation
> in ancient Egyptian religion.

**Osiris AI** is a thin wrapper around [LangChain4j](https://github.com/langchain4j/langchain4j),
allowing you to easily interact with LLMs from Kotlin.

```kotlin
val modelFactory: ModelFactory =
  modelFactory {
    openAiApiKey = ProtectedString("...")
  }

val messages = listOf(UserMessage("What's 2+2?"))
val (response) = llm(modelFactory.openAi("gpt-4.1-nano"), messages)

response.response<String>()
// 2 + 2 equals 4.
```

## Features

<details>

<summary>Basic usage</summary>

```kotlin
val messages = listOf(UserMessage("What's 2+2?"))
val (response) = llm(modelFactory.openAi("gpt-4.1-nano"), messages)

response.response<String>()
// 2 + 2 equals 4.
```

</details>

<details>

<summary>Tools</summary>

```kotlin
object WeatherTool : Tool<WeatherTool.Input, WeatherTool.Output>("weather") {
   data class Input(
      @LlmSchema.Description("The city to get the weather for.")
      val location: String,
   )

   data class Output(
      val temperature: String,
      val conditions: String,
   )

   override val description: String = "Gets the weather."

   override suspend fun execute(input: Input): Output =
      TODO("Your implementation.")
}

val messages = listOf(UserMessage("What's the weather in Calgary?"))
val (response) = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = messages,
  tools = listOf(WeatherTool),
)

response.response<String>()
// The weather in Calgary is sunny with a temperature of 15 degrees Celsius.
```

</details>

<details>

<summary>Structured output</summary>

```kotlin
@LlmSchema.SchemaName("person")
data class Person(
  val name: String,
  val age: Int,
)

val messages = listOf(
  UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
  SystemMessage("Provide a JSON representation of the person matching this description."),
)
val (response) = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = messages,
  responseType = Person::class,
)

response.response<Person>()
// Person(name=Jeff Hudson, age=29)
```

</details>

<details>

<summary>Evals</summary>

```kotlin
val messages = listOf(
  UserMessage("What's the weather in Calgary?"),
)
val (response) = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
   messages = messages,
  tools = listOf(WeatherTool),
)

evaluate(
  model = modelFactory.openAi("o3-mini"),
  messages = messages + response,
  criteria = "Should say that the weather in Calgary is 15 degrees Celsius and sunny.",
)
```

</details>

<details>

<summary>Agents</summary>

```kotlin
object TrackOrderTool : Tool<TrackOrderTool.Input, String>("track_order") {
  data class Input(
    val orderId: String,
  )

  override suspend fun execute(input: Input): String =
    TODO("Your implementation.")
}

val instructionsBuilder: InstructionsBuilder =
  instructionsBuilder(includeDefaultInstructions = true) {
    add(
      """
        # Ecommerce store
   
        The user is a customer at an ecommerce store.
      """.trimIndent(),
    )
  }

val chatbot: Agent =
  agent("chatbot") {
    model = testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }
    instructions = instructionsBuilder.create(
      """
        # Your role and task

        You are the store's really smart AI assistant.
        Your task is to use tools to comprehensively answer the user's question.
      """.trimIndent(),
    )
    tools += Consult("order_tracker")
  }

val orderTracker: Agent =
  agent("order_tracker") {
    description = "Use to track an order."
    model = testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }
    instructions = instructionsBuilder.create(
      """
        # Your role and task

        You are the store's data analyst.
        Your role is to track orders.
      """.trimIndent(),
    )
    tools += TrackOrderTool
  }

val network: Network =
  network("network") {
    entrypoint = chatbot.name
    agents += chatbot
    agents += orderTracker
  }

val messages = listOf(
  UserMessage("Where are my orders? The IDs are ord_0 and ord_1."),
)
val response = network.run(messages).response().last()
response.convert<String>()
// Your order with ID ord_0 has not been shipped yet, and your order with ID ord_1 is currently in transit.
```

</details>

## Getting started

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
  implementation("software.airborne.osiris:osiris-agentic:0.6.0")
  implementation("software.airborne.osiris:osiris-core:0.6.0")
  implementation("software.airborne.osiris:osiris-evaluator:0.6.0")
  implementation("software.airborne.osiris:osiris-open-ai:0.6.0")
}
```

## Project information

### Major dependencies

- Gradle 8.14
- Kotlin 2.1
- Java 21
- Kairo 5.2
- Langchain4j 1.0

### Style guide

Please follow the [Kairo style guide](https://github.com/hudson155/kairo/blob/main/docs/style-guide.md).

### Chores

See [chores](./docs/chores.md).

## Releasing

1. Familiarize yourself with [semantic versioning](https://semver.org/).
2. Create a new branch called `release/X.Y.Z`.
3. Bump the version in [osiris-ai-publish.gradle.kts](./buildSrc/src/main/kotlin/osiris-ai-publish.gradle.kts).
4. Commit "Release X.Y.Z".
5. Create and merge a PR "Release X.Y.Z". No description is necessary.
6. [Draft a new release](https://github.com/hudson155/osiris-ai/releases/new).
   Create a new tag `vX.Y.Z`. Generate release notes.
7. Manually run `./gradlew publish` on `main` after merging.
