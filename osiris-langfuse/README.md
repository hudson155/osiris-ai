# Osiris for OpenAI

Osiris has a tight integration with [Langfuse](https://langfuse.com/).
This module must be installed for basic Langfuse connectivity,
but a peer module must be installed to see real functionality.

- See [osiris-langfuse-prompt](./prompt)
  for prompt management.
- See [osiris-langfuse-tracing](./prompt)
  for tracing.

## Installation

`software.airborne.osiris:osiris-langfuse:0.14.0`

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

  /**
   * Also include at least one of the following peers.
   */
  implementation("software.airborne.osiris:osiris-langfuse-prompt:0.14.0") 
  implementation("software.airborne.osiris:osiris-langfuse-tracing:0.14.0")

  /**
   * Also include one of the following,
   * depending on whether you're using the agentic framework or the core module.
   */
  implementation("software.airborne.osiris:osiris-agentic:0.14.0") 
  implementation("software.airborne.osiris:osiris-core:0.14.0")
}
```

</details>
