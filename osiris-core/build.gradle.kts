plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(project(":osiris-schema"))

  api(libs.kairoCoroutines)
  implementation(libs.kairoLogging)
  api(libs.kairoProtectedString)
  implementation(libs.kairoReflect)
  implementation(libs.kairoSerialization)
  api(libs.langchain)

  testImplementation(project(":osiris-testing"))

  testImplementation(libs.kairoLoggingTesting)
}
