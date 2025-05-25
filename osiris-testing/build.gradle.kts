plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(project(":osiris-core"))

  implementation(libs.kairoEnvironmentVariableSupplier)
  implementation(libs.kairoSerialization)
  api(libs.kairoTesting)
  api(libs.langchainGemini)
  api(libs.langchainOpenAi)
}
