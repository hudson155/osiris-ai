# Osiris AI

Osiris is a Kotlin wrapper for [Langchain](https://github.com/langchain4j/langchain4j).

## Project information

### Major dependencies

- Gradle 8.14
- Kotlin 2.1
- Java 21
- Kairo 4.2
- Langchain4j 1.0

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
   Create a new tag `vX.Y.Z`. Generate release notes.
7. Manually run `./gradlew publish` on `main` after merging.
