plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(project(":osiris-core"))

  implementation(libs.kairoSerialization)
  implementation(libs.kairoTesting)
}
