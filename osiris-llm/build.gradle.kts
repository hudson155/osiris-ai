plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  api(project(":osiris-agent"))

  implementation(libs.kairo.logging)
  api(libs.kairo.serialization)
  api(libs.langchain)
}
