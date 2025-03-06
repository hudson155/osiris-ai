# Chores

## Update all dependencies

Update dependencies in the following locations.
Review the release notes for any relevant changes.

**Major dependencies:**

- **Gradle:** [gradle-wrapper.properties](../gradle/wrapper/gradle-wrapper.properties).
  Use the command `./gradlew wrapper --gradle-version=<version>` to upgrade.
- **Kotlin:** [buildSrc/build.gradle.kts](../buildSrc/build.gradle.kts).
- **Java:** [osiris-ai.gradle.kts](../buildSrc/src/main/kotlin/osiris-ai.gradle.kts).
  The Java version must correspond with the Kotlin version.
  If it doesn't, you'll get a compilation error.
- **Detekt:** [buildSrc/build.gradle.kts](../buildSrc/build.gradle.kts).
  When upgrading Detekt versions, review `default-detekt-config.yaml` as well as the changelog.
  Make corresponding changes as necessary in [the config file](../.detekt/config.yaml).
- **Kairo:** [libs.versions.toml](../gradle/libs.versions.toml).
  1. Familiarize yourself with [semantic versioning](https://semver.org/).
  2. Create a new branch called `feature/kairo`.
  3. Upgrade the version in [libs.versions.toml](../gradle/libs.versions.toml) and the root README.
  4. Review the [release notes diff](https://github.com/hudson155/kairo/releases) and make any necessary changes.
     This includes changes to the Detekt config.
  5. Commit "Upgrade to Kairo X.Y.Z".

**Other dependencies:**

- Other dependencies specified in [buildSrc/build.gradle.kts](../buildSrc/build.gradle.kts).
- Other dependencies specified in [libs.versions.toml](../gradle/libs.versions.toml).

## Delete stale GitHub branches

There probably aren't a lot, but it's nice to delete them every once in a while.
