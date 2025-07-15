# Osiris & Langfuse

Osiris has a tight integration with [Langfuse](https://langfuse.com/).
This module must be installed for basic Langfuse connectivity,
but a peer module must be installed to see real functionality.

## Installation

`software.airborne.osiris:osiris-langfuse:0.28.2`

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
  implementation("software.airborne.osiris:osiris-langfuse:0.28.2")

  /**
   * Also include at least one of the following peers.
   */
  implementation("software.airborne.osiris:osiris-langfuse-prompt:0.28.2") 
  implementation("software.airborne.osiris:osiris-langfuse-tracing:0.28.2")

  /**
   * Also include one of the following,
   * depending on whether you're using the chat module or the agentic framework.
   */
  implementation("software.airborne.osiris:osiris-chat:0.28.2")
  implementation("software.airborne.osiris:osiris-agentic:0.28.2")
}
```

</details>

## Usage

Create a Langfuse instance.

```kotlin
val langfuse = Langfuse(
  url = "https://us.cloud.langfuse.com/",
  publicKey = "pk-lf-...",
  secretKey = ProtectedString("sk-lf-..."),
)
```

- See [osiris-langfuse-prompt](./prompt)
  for prompt management.
- See [osiris-langfuse-tracing](./prompt)
  for tracing.
