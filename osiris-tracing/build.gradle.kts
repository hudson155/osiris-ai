plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(libs.kairoCoroutines)
  implementation(libs.kairoLogging)
}
