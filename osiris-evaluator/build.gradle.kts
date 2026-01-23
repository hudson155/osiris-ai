plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  compileOnly(project(":osiris-agent"))
  compileOnly(project(":osiris-llm"))
  implementation(project(":osiris-schema"))

  compileOnly(libs.kairo.testing)
}
