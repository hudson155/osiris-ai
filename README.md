# Osiris AI

> [Osiris](https://en.wikipedia.org/wiki/Osiris)
> was the god of fertility, agriculture, the afterlife, the dead, resurrection, life, and vegetation
> in ancient Egyptian religion.

**Osiris AI** enables easy & robust LLM integration from Kotlin.
There are 2 different interaction patterns.

### Core module

Osiris's [core module](./osiris-core) favors simplicity,
supporting basic Kotlin-idiomatic LLM interaction.

```kotlin
val model = modelFactory.openAi("gpt-4.1-nano")
val messages = listOf(
   UserMessage("What's 2+2?"),
)

val response = llm(model, messages)

response.convert<String>()
// 2 + 2 equals 4.
```

Visit the [core module](./osiris-core)'s documentation for more details.

### Agentic framework

Osiris's [agentic framework](./osiris-agentic) lets you build complex systems.
This is similar to the [OpenAI Agents SDK](https://openai.github.io/openai-agents-python/)
or [LangGraph](https://langchain-ai.github.io/langgraph/).
Splitting up workloads across a Network of smaller, more focused Agents
helps deliver better responses faster responses, and improved traceability.

```kotlin
val ecommerceChatbot = agent("ecommerce_chatbot") { ... }

val ecommerceOrderTracker = agent("ecommerce_order_tracker") { ... }

val network =
  network("network") {
    entrypoint = ecommerceChatbot.name
    agents += ecommerceChatbot
    agents += ecommerceOrderTracker
  }

val response = network.run(
  messages = listOf(
    UserMessage("Where are my orders? The IDs are ord_0 and ord_1."),
  ),
)

response.convert<String>()
// Your order with ID ord_0 has not been shipped yet, and your order with ID ord_1 is currently in transit.
```

Visit the [agentic framework](./osiris-agentic)'s documentation for more details.

## Installation

`software.airborne.osiris:osiris-core:0.16.0`\
or `software.airborne.osiris:osiris-agentic:0.16.0`

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
   /**
    * Include one of the following,
    * depending on whether you're using the core module or the agentic framework.
    */
  implementation("software.airborne.osiris:osiris-core:0.16.0")
   implementation("software.airborne.osiris:osiris-agentic:0.16.0")
}
```

</details>

## Features

These features are supported by both
the [core module](./osiris-core) and the [agentic framework](./osiris-agentic).
Specifics on how to use either of those interaction patterns can be found in the respective documentation.

### Automatic schema generation

Osiris supports automatic LLM (OpenAPI) **schema generation** for **structured output** and **tool calls**,
so you don't need to manage schemas yourself.

A typical schema looks like this

```kotlin
@LlmSchema.SchemaName("person") // Required!
data class Person(
  @LlmSchema.Description("Their full name.") // Optional additional context for the LLM.
  val name: String,
  val age: Int,
)
```

In addition to primitives,
automatic schema generation also supports
**lists**, **nested objects**, and **polymorphism**.

See [osiris-schema](./osiris-schema)
for full documentation.

### Tools

To make Tools available to the LLM,
extend the `Tool` class.

- More details for [osiris-core](./osiris-core/README.md#using-tools)
- More details for [osiris-agentic](./osiris-agentic/README.md#tool)

### Structured output

Structured output is supported through [osiris-schema](./osiris-schema).

- More details for [osiris-core](./osiris-core/README.md#structured-output)
- More details for [osiris-agentic](./osiris-agentic/README.md#structured-output)

### Prompt management

```kotlin
val instructions = Instructions { "The odds of the Oilers winning tonight are {{odds}}." }
instructions.compile {
  put("odds", "42.8%")
}
// The odds of the Oilers winning tonight are 42.8%.
```

See [osiris-prompt](./osiris-prompt)
for full documentation.

### Evals

`evaluate()` lets you write basic evals.

```kotlin
@Test
fun test(): Unit = runTest {
  val response = llm(
    model = modelFactory.openAi("gpt-4.1-nano"),
    messages = listOf(
      UserMessage("What's the weather in Calgary?"),
    ),
    tools = listOf(WeatherTool()),
  )

  evaluate(
    model = modelFactory.openAi("o3-mini"),
    messages = messages + response,
    criteria = "Should say that the weather in Calgary is 15 degrees Celsius and sunny.",
  )
}
```

See [osiris-evaluator](./osiris-evaluator)
for full documentation.

### Tracing

You can add arbitrary listeners to LLM requests and agentic executions,
as well as pipe those listeners to tracing tools such as Langfuse.

See [osiris-tracing](./osiris-tracing)
for general tracing documentation,
or [osiris-langfuse-tracing](./osiris-langfuse/tracing)
for a Langfuse quickstart.

## Project information

### Major dependencies

- Gradle 8.14
- Kotlin 2.1
- Java 21
- Kairo 5.7
- Langchain4j 1.0

### Style guide

Please follow the [Kairo style guide](https://github.com/hudson155/kairo/blob/main/docs/style-guide.md).

## Releasing

1. Familiarize yourself with [semantic versioning](https://semver.org/).
2. Create a new branch called `release/X.Y.Z`.
3. Bump the version in [osiris-ai-publish.gradle.kts](./buildSrc/src/main/kotlin/osiris-ai-publish.gradle.kts).
4. Commit "Release X.Y.Z".
5. Create and merge a PR "Release X.Y.Z". No description is necessary.
6. [Draft a new release](https://github.com/hudson155/osiris-ai/releases/new).
   Create a new tag `vX.Y.Z`. Generate release notes.
7. Manually run `./gradlew publish` on `main` after merging.
