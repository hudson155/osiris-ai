plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(libs.guava)

  testImplementation(libs.kairoTesting)
}
