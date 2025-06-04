plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(project(":osiris-core"))

  api(libs.langchainOpenAi)
}
