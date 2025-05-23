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

  testImplementation(project(":testing"))

  testImplementation(libs.kairoEnvironmentVariableSupplier)
  testImplementation(libs.langchainGemini)
  testImplementation(libs.langchainOpenAi)
}
