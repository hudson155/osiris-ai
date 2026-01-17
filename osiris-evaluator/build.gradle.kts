plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  compileOnly(project(":osiris-llm")) // Forced peer dependency.
  implementation(project(":osiris-schema"))

  implementation(libs.kairo.testing)
}
