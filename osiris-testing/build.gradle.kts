plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(project(":osiris-core"))

  implementation(libs.kairoSerialization)
  implementation(libs.kairoTesting)
}
