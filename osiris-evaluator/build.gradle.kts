plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(project(":osiris-chat"))

  implementation(libs.kairoTesting)
}
