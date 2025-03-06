# Build plugins

The following Gradle plugins help standardize the build process across Gradle modules.

- `osiris-ai`: Configures JVM Gradle modules.
  Unless and until multiplatform modules are introduced,
  this should be used in all Gradle modules.
- `osiris-ai-publish`: This plugin should be applied to modules that need to be published.
  They will be published to the artifact registry.
