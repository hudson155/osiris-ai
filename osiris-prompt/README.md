# Osiris Prompt

Osiris's **prompt module** helps with prompt management.

## Installation

`software.airborne.osiris:osiris-prompt:0.15.0`

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
  implementation("software.airborne.osiris:osiris-prompt:0.15.0")

  /**
   * Also include one of the following,
   * depending on whether you're using the core module or the agentic framework.
   */
  implementation("software.airborne.osiris:osiris-core:0.15.0")
  implementation("software.airborne.osiris:osiris-agentic:0.15.0")
}
```

</details>

## Usage

```kotlin
val prompt = "The odds of the Oilers winning tonight are {{odds}}."
compilePrompt(prompt) {
  put("odds", "42.8%")
}
// The odds of the Oilers winning tonight are 42.8%.
```
