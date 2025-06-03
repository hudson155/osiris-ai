plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(project(":osiris-core"))
  implementation(project(":osiris-testing"))

  implementation(libs.kairoCoroutines)
  implementation(libs.kairoSerialization)
  implementation(libs.kairoTesting)
}
