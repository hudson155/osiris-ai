# Osiris Agentic

Osiris's **agentic module** supports LLM-based agentic frameworks,
similar to the [OpenAI Agents SDK](https://openai.github.io/openai-agents-python/)
or [LangGraph](https://langchain-ai.github.io/langgraph/).

Splitting up workloads across a Network of smaller, more focused Agents
helps deliver better responses faster responses, and improved traceability.

## Installation

`software.airborne.osiris:osiris-agentic:0.15.0`

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
  implementation("software.airborne.osiris:osiris-agentic:0.15.0")
}
```

</details>

## Overview

Osiris's Agentic Framework lets you build complex agentic apps using a simple API.

- **Agents** have a specific role/task,
  and are equipped with instructions and Tools specific to that role/task.
- Agents live within a **Network**,
  which enables Agents to consult one another to complete complex tasks.

## Usage

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

#### Langchain4j

Osiris is built atop [Langchain4j](https://github.com/langchain4j/langchain4j).
Some of the classes you'll use with Osiris
(`ChatModel`, `ChatMessage`, `ChatRequest`, `ChatResponse`, etc.)
are actually from Langchain4j.

### Shared instructions

It's usually helpful for Agents to have a shared preamble in their instructions.
Osiris supports this using `InstructionsBuilder`.

```kotlin
val instructionsBuilder =
  instructionsBuilder(includeDefaultInstructions = true) {
    add {
      """
        # Ecommerce store
        
        The user is a customer at an ecommerce store.
      """.trimIndent()
    }
  }
```

In this example, the default instructions will be included too
(due to `includeDefaultInstructions = true`).

Each Agent will therefore have this preamble prepended to its instructions.

```markdown
# The system

You're a part of a multi-agent system.
You can consult other agents.
When consulting other agents, succinctly tell them what to do or what you need.
Don't tell them how to do their job.

# Ecommerce store

The user is a customer at an ecommerce store.
```

### Entrypoint Agent

The first Agent will be our _entrypoint_ chatbot.
This is the first Agent that will be visited within the Network.
Sometimes this is referred to as a "manager" or "orchestrator".

```kotlin
val ecommerceChatbot =
  agent("ecommerce_chatbot") {
    model = modelFactory.openAi("gpt-4.1-nano")
    instructions = instructionsBuilder.build {
      """
        # Your role and task
        
        You are the store's really smart AI assistant.
        Your task is to use tools to comprehensively answer the user's question.
      """.trimIndent()
    }
    tools += Consult("ecommerce_order_tracker") // We still need to define this Agent.
  }
```

The `ecommerce_chatbot` Agent has its own set of instructions that will follow the preamble.
It's also allowed to _consult_ a specialist Agent called `ecommerce_order_tracker`.
We will define this Agent next.

### Specialist Agent

The _specialist_ Agent `ecommerce_order_tracker` is defined similarly to the entrypoint Agent,
but it's also given access to a Tool.

```kotlin
val ecommerceOrderTracker =
  agent("ecommerce_order_tracker") {
    description = "Use to track an order."
    model = modelFactory.openAi("gpt-4.1-nano")
    instructions = instructionsBuilder.build {
      """
        # Your role and task
        
        You are the store's data analyst.
        Your role is to track orders.
      """.trimIndent()
    }
    tools += TrackOrderTool() // We still need to define this Tool.
  }
```

### Tool

Tools are defined the same way as they are in [osiris-core](../osiris-core/README.md#using-tools).

```kotlin
class TrackOrderTool : Tool<TrackOrderTool.Input>("track_order") {
  data class Input(
    val orderId: String,
  )

  override suspend fun execute(input: Input): String =
    when (input.orderId) {
      "ord_0" -> "Not shipped yet."
      "ord_1" -> "In transit."
      else -> "Unknown order."
    }
}
```

The Tool name and description are made available to the LLM.

The input schema should conform to [osiris-schema](../osiris-schema).
Optionally use `@LlmSchema.Description` to provide additional context to the LLM.

### Network

Now that we have both Agents & our Tool defined, we can put the Network together, specifying the correct entrypoint.

```kotlin
val network =
  network("network") {
    entrypoint = ecommerceChatbot.name
    agents += ecommerceChatbot
    agents += ecommerceOrderTracker
  }
```

### Make a request

Make your request by calling `Network.run()`.

```kotlin
val response = network.run(
  messages = listOf(
    UserMessage("Where are my orders? The IDs are ord_0 and ord_1."),
  ),
)
```

Then convert the response to the appropriate type using `.convert()`.

```kotlin
response.convert<String>()
// Your order with ID ord_0 has not been shipped yet, and your order with ID ord_1 is currently in transit.
```

## Advanced usage

### Structured output

Structured output is supported through [osiris-schema](../osiris-schema).

```kotlin
@LlmSchema.SchemaName("person")
data class Person(
  val name: String,
  val age: Int,
)

val personCreator =
  agent("person_creator") {
    model = modelFactory.openAi("gpt-4.1-nano")
    instructions = Instructions { "Provide a JSON representation of the person matching this description." }
    responseType = Person::class
  }
```

### Asynchronous updates

Just like ChatGPT streams output back to the user,
you can stream output by using `Network.runAsync()` instead of `Network.run()`.

```kotlin
val messages = listOf(
  UserMessage("Where are my orders? The IDs are ord_0 and ord_1."),
)
network.runAsync(messages).collect { networkEvent ->
  when (networkEvent) {
    is NetworkEvent.Event -> {
      val event = networkEvent.event
      TODO("Handle any events you want.")
    }
    is NetworkEvent.Response -> {
      val response = networkEvent.response
      response.convert<String>()
      // Your order with ID ord_0 has not been shipped yet, and your order with ID ord_1 is currently in transit.
    }
  }
}
```

### Tracing

You can add tracing by adding Listeners to the Network

```kotlin
val network =
  network("network") {
    entrypoint = ecommerceChatbot.name
    agents += ecommerceChatbot
    agents += ecommerceOrderTracker
    listener(langfuse.trace())
  }
```

For more details, see [osiris-tracing](../osiris-tracing).
