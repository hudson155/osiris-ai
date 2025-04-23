plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(libs.kairoCoroutines)
  implementation(libs.kairoLogging)
  api(libs.langchain)

  testImplementation(libs.kairoLoggingTesting)
  testImplementation(libs.kairoReflect)
  testImplementation(libs.kairoSerialization)
  testImplementation(libs.kairoTesting)
  testImplementation(libs.langchainGemini)
  testImplementation(libs.langchainOpenAi)
}
