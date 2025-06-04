plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(project(":osiris-core"))

  api(libs.kairoCoroutines)
  implementation(libs.kairoLogging)

  testImplementation(project(":osiris-evaluator"))
  testImplementation(project(":osiris-open-ai"))

  testImplementation(libs.kairoEnvironmentVariableSupplier)
  testImplementation(libs.kairoLazySupplier)
  testImplementation(libs.kairoLoggingTesting)
  testImplementation(libs.kairoSerialization)
  testImplementation(libs.kairoTesting)
}
