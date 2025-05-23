plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(libs.kairoLogging)

  testImplementation(libs.kairoLoggingTesting)
  testImplementation(libs.kairoTesting)
}
