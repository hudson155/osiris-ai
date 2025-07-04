plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(libs.kairoId)
  implementation(libs.kairoReflect)
  implementation(libs.langchain)

  testImplementation(libs.kairoTesting)
}
