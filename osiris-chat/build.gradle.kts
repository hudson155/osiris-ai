plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(project(":osiris-core"))
  api(project(":osiris-prompt"))
  api(project(":osiris-schema"))
  api(project(":osiris-tracing"))

  implementation(libs.kairoCoroutines)
  api(libs.kairoLazySupplier)
  implementation(libs.kairoLogging)
  api(libs.kairoProtectedString)
  implementation(libs.kairoReflect)
  implementation(libs.kairoUtil)

  testImplementation(project(":osiris-evaluator"))
  testImplementation(project(":osiris-open-ai"))

  testImplementation(libs.kairoEnvironmentVariableSupplier)
  testImplementation(libs.kairoLoggingTesting)
  testImplementation(libs.kairoTesting)
}
