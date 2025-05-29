plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  api(project(":osiris-schema"))

  api(libs.kairoProtectedString)
  implementation(libs.kairoSerialization)
  api(libs.langchain)
}
