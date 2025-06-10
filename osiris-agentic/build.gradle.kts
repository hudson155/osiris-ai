plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(project(":osiris-core"))

  api(libs.kairoCoroutines)

  testImplementation(project(":osiris-evaluator"))
  testImplementation(project(":osiris-open-ai"))
  testImplementation(project(":osiris-testing"))

  testImplementation(libs.kairoEnvironmentVariableSupplier)
  testImplementation(libs.kairoLoggingTesting)
  testImplementation(libs.kairoSerialization)
}
