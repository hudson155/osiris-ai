plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(libs.kairoLogging)
  api(libs.openAi)
}
