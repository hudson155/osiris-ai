# Osiris AI

> [Osiris](https://en.wikipedia.org/wiki/Osiris)
> was the god of fertility, agriculture, the afterlife, the dead, resurrection, life, and vegetation
> in ancient Egyptian religion.

**Osiris AI** enables simple & robust LLM interaction from Kotlin.

Osiris is built atop [Langchain4j](https://github.com/langchain4j/langchain4j).
Many of the classes you'll use with Osiris
(`ChatModel`, `ChatMessage`, `ChatRequest`, `ChatResponse`, etc.)
are actually from Langchain4j.

## Documentation

[See the GitHub wiki](https://github.com/hudson155/osiris-ai/wiki) for complete documentation.

### [Simple LLM usage](https://github.com/hudson155/osiris-ai/wiki/Simple-LLM-usage)

The `llm()` function enables simple LLM interaction from anywhere in your code.

```kotlin
val model = modelFactory.openAi("gpt-4.1-nano")
val messages = listOf(
  UserMessage("What's 2+2?"),
)

val response = llm(model, messages)

response.convert<String>()
// 2 + 2 equals 4.
```

Learn more about [Simple LLM usage](https://github.com/hudson155/osiris-ai/wiki/Simple-LLM-usage).

## Project information

### Major dependencies

- Gradle 8.14
- Kotlin 2.2
- Java 21
- Kairo 5.15
- Langchain4j 1.1

### Style guide

Please follow the [Kairo style guide](https://github.com/hudson155/kairo/blob/main/docs/style-guide.md).

- **Product terminology:**
  - Treat Osiris _Agents_, _Tools_, _Guardrails_, and _Networks_
    as proper nouns (the first letter should be capitalized).
  - Treat Osiris _Listeners_ and _Tracers_ as proper nouns (the first letter should be capitalized).

## Releasing

1. Familiarize yourself with [semantic versioning](https://semver.org/).
2. Create a new branch called `release/X.Y.Z`.
3. Bump the version in [osiris-ai-publish.gradle.kts](./buildSrc/src/main/kotlin/osiris-ai-publish.gradle.kts).
4. Commit "Release X.Y.Z".
5. Create and merge a PR "Release X.Y.Z". No description is necessary.
6. [Draft a new release](https://github.com/hudson155/osiris-ai/releases/new).
   Create a new tag `vX.Y.Z`. Generate release notes.
7. Manually run `./gradlew publish` on `main` after merging.
