# Osiris & Langfuse: Prompt management

Osiris has a tight integration with [Langfuse](https://langfuse.com/).
See [osiris-langfuse](../) for basic connectivity.

This module integrates with Langfuse's prompt management product.
https://langfuse.com/docs/prompts/get-started

## Installation

`software.airborne.osiris:osiris-langfuse-prompt:0.14.0`

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
  implementation("software.airborne.osiris:osiris-langfuse:0.14.0")
  implementation("software.airborne.osiris:osiris-langfuse-prompt:0.14.0")

  /**
   * Also include the following,
   * assuming you're using the agentic framework.
   */
  implementation("software.airborne.osiris:osiris-agentic:0.14.0") 
}
```

</details>

## Usage

### Create a Langfuse instance

Create a Langfuse instance.

```kotlin
val langfuse = Langfuse(
  url = "https://us.cloud.langfuse.com/api/public/",
  publicKey = "pk-lf-...",
  secretKey = ProtectedString("sk-lf-..."),
)
```

### Access a prompt

Access a prompt when building your Agent.

```kotlin
val ecommerceChatbot =
  agent("ecommerce_chatbot") {
    model = modelFactory.openAi("gpt-4.1-nano")
    instructions = langfuse.prompt("ai_analyst").compile() // Nice!
    tools += Consult("ecommerce_order_tracker")
  }
```
