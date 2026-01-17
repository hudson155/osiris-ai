plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  compileOnly(project(":osiris-agent")) // Forced peer dependency.
  compileOnly(project(":osiris-llm")) // Forced peer dependency.
  compileOnly(project(":osiris-schema")) // Forced peer dependency.

  implementation(libs.kairo.testing)
}
