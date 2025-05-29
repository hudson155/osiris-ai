plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(libs.kairoReflect)
  api(libs.langchain)

  testImplementation(libs.kairoTesting)
}
