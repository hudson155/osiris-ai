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

val response = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's 2+2?"),
  ),
)

response.convert<String>() // 2 + 2 equals 4.
```

## Features

<details>

<summary>Basic usage</summary>

```kotlin
val response = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = listOf(
    UserMessage("What's 2+2?"),
  ),
)

response.convert<String>() // 2 + 2 equals 4.
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

val response = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  tools = listOf(WeatherTool),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
)

response.convert<String>() // The weather in Calgary is sunny with a temperature of 15 degrees Celsius.
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

val response = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  responseType = Person::class,
  messages = listOf(
    UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
    SystemMessage("Provide a JSON representation of the person matching this description."),
  ),
)

response.convert<String>() // Person(name=Jeff Hudson, age=29)
```

</details>

<details>

<summary>Evals</summary>

```kotlin
val response = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  tools = listOf(WeatherTool),
  messages = listOf(
    UserMessage("What's the weather in Calgary?"),
  ),
)

evaluate(
  model = modelFactory.openAi("o3-mini"),
  response = response.convert<String>(),
  criteria = "Should say that the weather in Calgary is 15 degrees Celsius and sunny.",
)
```

</details>

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
