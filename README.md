# Osiris AI

Building robust production-ready AI integrations goes beyond just sending requests to OpenAI.
You need a seamless interface, efficient tool calls, logging and debugging capabilities, and reliable error management.
Osiris AI is a Kotlin-based OpenAI wrapper designed to streamline and simplify your AI integrations.

Osiris AI provides:

1. **First-class support for tool calls.**
   Define your tools and make a single call to Osiris, not N calls.

2. **Automatic type conversion**,
   ensuring structured output is effortlessly mapped to your data types.

3. **Built-in retries**, handling transient errors gracefully.
   Includes both parallel tries and sequential tries.

4. **Asynchronous operation through Kotlin `Flow`s**,
   complete with asynchronous progress updates and debug information.

Focus on what matters: build intelligent applications, not plumbing.

## Project information

### Major dependencies

- Gradle 8.13
- Kotlin 2.1
- Java 21
- Kairo 4.0

### Style guide

Please follow the [Kairo style guide](https://github.com/hudson155/kairo/blob/main/docs/style-guide.md).

### Chores

See [chores](./docs/chores.md).

## Releasing

1. Familiarize yourself with [semantic versioning](https://semver.org/).
2. Create a new branch called `release/X.Y.Z`.
3. Bump the version in [osiris-ai-publish.gradle.kts](./buildSrc/src/main/kotlin/osiris-ai-publish.gradle.kts).
4. Commit "Release X.Y.Z".
5. Create and merge a PR "Release X.Y.Z". No description is necessary.
6. [Draft a new release](https://github.com/hudson155/osiris-ai/releases/new).
   Create a new tag `vX.Y.Z`.
   Title "Osiris X.Y.Z".
   Generate and prefix release notes.
7. Manually run `./gradlew publish` on `main` after merging.
