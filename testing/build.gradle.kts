plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(project(":core"))

  implementation(libs.kairoSerialization)

  implementation(libs.kairoEnvironmentVariableSupplier)
  api(libs.kairoLoggingTesting)
  api(libs.kairoTesting)
  implementation(libs.langchainGemini)
  implementation(libs.langchainOpenAi)
}
