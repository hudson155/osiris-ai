# Osiris Evaluator

Osiris's **evaluator module** lets you write basic evals.

## Installation

`software.airborne.osiris:osiris-evaluator:0.26.0`

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
  implementation("software.airborne.osiris:osiris-evaluator:0.26.0")

  /**
   * Also include one of the following,
   * depending on whether you're using the chat module or the agentic framework.
   */
  implementation("software.airborne.osiris:osiris-chat:0.26.0")
  implementation("software.airborne.osiris:osiris-agentic:0.26.0")
}
```

</details>

## Usage

`evaluate()` is the primary function.
Use this within a test.

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
    model = modelFactory.openAi("o4-mini"),
    messages = messages + response,
    criteria = "Should say that the weather in Calgary is 15 degrees Celsius and sunny.",
  )
}
```

The evaluation will be done by the given `model`.
It's best to use a reasoning model here.

The `criteria` can be multiple lines of text,
but larger criteria may also be broken up into multiple evaluations on the same response.

#### Failure

If the eval fails, the test will fail.
The LLM will provide a failure reason, which you'll see in your test logs.

```text
The answer stated that the weather in Calgary is -30 degrees Celsius with snowing conditions, whereas the criteria specify that it should be 15 degrees Celsius and sunny.
expected:<true> but was:<false>
Expected :true
Actual   :false
<Click to see difference>
```
