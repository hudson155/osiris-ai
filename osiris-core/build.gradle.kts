plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(project(":osiris-schema"))

  implementation(libs.kairoCoroutines)
  implementation(libs.kairoEnvironmentVariableSupplier)
  api(libs.kairoProtectedString)
  implementation(libs.kairoReflect)
  implementation(libs.kairoSerialization)
  api(libs.langchain)

  testImplementation(project(":osiris-open-ai"))

  testImplementation(libs.kairoTesting)
}
