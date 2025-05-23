plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(project(":core"))

  implementation(libs.kairoSerialization)

  api(libs.kairoLoggingTesting)
  api(libs.kairoTesting)
}
