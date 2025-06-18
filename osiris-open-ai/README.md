# Osiris for OpenAI

Simple quality of life DSL for OpenAI.

## Installation

`software.airborne.osiris:osiris-open-ai:0.16.1`

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
  implementation("software.airborne.osiris:osiris-open-ai:0.16.1")

  /**
   * Also include one of the following,
   * depending on whether you're using the core module or the agentic framework.
   */
  implementation("software.airborne.osiris:osiris-core:0.16.1")
  implementation("software.airborne.osiris:osiris-agentic:0.16.1")
}
```

</details>

## Usage

Instead of defining your model using Langchain4j's syntax,

```kotlin
OpenAiChatModel.builder().apply {
  modelName("gpt-4.1-nano")
  apiKey("...")
  strictJsonSchema(true)
  strictTools(true)
}.build()
```

you can instead include your OpenAI API key in your model factory

```kotlin
val modelFactory =
  modelFactory {
    openAiApiKey = ProtectedString("...")
  }
```

and then use the simple DSL

```kotlin
modelFactory.openAi("gpt-4.1-nano")
```

whenever you need a model.
