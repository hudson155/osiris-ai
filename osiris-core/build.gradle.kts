plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(project(":osiris-schema"))

  api(libs.kairoCoroutines)
  api(libs.kairoLazySupplier)
  api(libs.kairoLogging)
  api(libs.kairoProtectedString)
  implementation(libs.kairoReflect)
  implementation(libs.kairoSerialization)
  implementation(libs.kairoUtil)
  api(libs.langchain)

  testImplementation(project(":osiris-evaluator"))
  testImplementation(project(":osiris-open-ai"))

  testImplementation(libs.kairoEnvironmentVariableSupplier)
  testImplementation(libs.kairoLoggingTesting)
  testImplementation(libs.kairoTesting)
}
