# Osiris Prompt

Osiris's **prompt module** helps with prompt management.

## Installation

Included by default with both [osiris-core](../osiris-core) and [osiris-agentic](../osiris-agentic).

## Usage

```kotlin
val prompt = "The odds of the Oilers winning tonight are {{odds}}."
compilePrompt(prompt) {
  put("odds", "42.8%")
}
// The odds of the Oilers winning tonight are 42.8%.
```
