plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(project(":open-ai"))

  implementation(libs.kairoCoroutines)
  implementation(libs.kairoLogging)
  implementation(libs.kairoReflect)
  implementation(libs.kairoSerialization)

  testImplementation(libs.kairoLoggingTesting)
  testImplementation(libs.kairoTesting)
}
