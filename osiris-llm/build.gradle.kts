plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  compileOnly(project(":osiris-agent")) // Forced peer dependency.

  implementation(libs.kairo.logging)
  api(libs.kairo.serialization)
  api(libs.langchain)
}
