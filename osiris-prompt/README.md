# Osiris Prompt

Osiris's **prompt module** helps with prompt management.

## Installation

Included by default with both [osiris-core](../osiris-core) and [osiris-agentic](../osiris-agentic).

## Usage

```kotlin
val instructions = "The odds of the Oilers winning tonight are {{odds}}."
instructions.compile {
  put("odds", "42.8%")
}
// The odds of the Oilers winning tonight are 42.8%.
```
