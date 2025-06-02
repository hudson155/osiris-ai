plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(project(":osiris-schema"))

  implementation(libs.kairoCoroutines)
  api(libs.kairoProtectedString)
  implementation(libs.kairoReflect)
  implementation(libs.kairoSerialization)
  api(libs.langchain)

  testImplementation(project(":osiris-evaluator"))
  testImplementation(project(":osiris-open-ai"))
  testImplementation(project(":osiris-testing"))

  testImplementation(libs.kairoEnvironmentVariableSupplier)
  testImplementation(libs.kairoTesting)
}
