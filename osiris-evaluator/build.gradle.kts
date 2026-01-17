plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  compileOnly(project(":osiris-agent")) // Forced peer dependency.
  compileOnly(project(":osiris-llm")) // Forced peer dependency.
  implementation(project(":osiris-schema"))

  compileOnly(libs.kairo.testing) // Forced peer dependency.
}
