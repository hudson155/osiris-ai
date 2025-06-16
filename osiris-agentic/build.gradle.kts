plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(project(":osiris-core"))

  implementation(libs.kairoCoroutines)
  implementation(libs.kairoLogging)

  testImplementation(project(":osiris-evaluator"))
  testImplementation(project(":osiris-langfuse"))
  testImplementation(project(":osiris-langfuse:tracing"))
  testImplementation(project(":osiris-open-ai"))

  testImplementation(libs.kairoEnvironmentVariableSupplier)
  testImplementation(libs.kairoLoggingTesting)
  testImplementation(libs.kairoSerialization)
  testImplementation(libs.kairoTesting)
}
