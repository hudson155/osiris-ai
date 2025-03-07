# Osiris AI

Osiris AI is a Kotlin OpenAI wrapper.

Limitations:
- `n` is used for parallel tries, so we can't actually return multiple results.

## Project information

### Major dependencies

- Gradle 8.10
- Kotlin 2.0
- Java 21
- Kairo 0.77

### Style guide

Please follow the [Kairo style guide](https://github.com/hudson155/kairo/blob/main/docs/style-guide.md).

### Chores

See [chores](./docs/chores.md).

## Contributing

**Run checks (tests and lint):**

```shell
./gradlew check
```

**Full build:**

```shell
./gradlew build
```

### Releasing

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
