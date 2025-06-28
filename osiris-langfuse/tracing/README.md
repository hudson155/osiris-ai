# Osiris & Langfuse: Tracing

Osiris has a tight integration with [Langfuse](https://langfuse.com/).
See [osiris-langfuse](../) for basic connectivity.

This module integrates with Langfuse's tracing product:
https://langfuse.com/docs/prompts/get-started.

Note: Custom events are not yet supported by this module.

## Installation

`software.airborne.osiris:osiris-langfuse-tracing:0.24.0`

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
  implementation("software.airborne.osiris:osiris-langfuse:0.24.0")
  implementation("software.airborne.osiris:osiris-langfuse-tracing:0.24.0")

  /**
   * Also include the following,
   * assuming you're using the agentic framework.
   */
  implementation("software.airborne.osiris:osiris-agentic:0.24.0") 
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

### Add tracing

Add Langfuse tracing to your Network.

```kotlin
val network =
  network("network") {
    entrypoint = ecommerceChatbot.name
    agents += ecommerceChatbot
    agents += ecommerceOrderTracker
    listener(langfuse.trace()) // Nice!
  }
```
