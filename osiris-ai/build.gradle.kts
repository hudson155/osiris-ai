plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(libs.kairoCoroutines)
  implementation(libs.kairoLogging)
  implementation(libs.kairoReflect)
  implementation(libs.kairoSerialization)
  api(libs.langchain)
  api(libs.langchainKotlin)

  testImplementation(libs.kairoEnvironmentVariableSupplier)
  testImplementation(libs.kairoLoggingTesting)
  testImplementation(libs.kairoTesting)
  testImplementation(libs.langchainGemini)
  testImplementation(libs.langchainOpenAi)
}
