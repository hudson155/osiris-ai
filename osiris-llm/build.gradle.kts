plugins {
  id("osiris-library")
  id("osiris-library-publish")
  id("osiris-ksp")
}

dependencies {
  compileOnly(project(":osiris-agent")) // Forced peer dependency.

  implementation(libs.kairo.logging)
  implementation(libs.kairo.reflect)
  api(libs.kairo.serialization)
  api(libs.langchain4j)

  testImplementation(project(":osiris-agent"))
  testImplementation(project(":osiris-evaluator"))
  testImplementation(project(":osiris-open-ai"))
  testImplementation(project(":osiris-schema"))

  testImplementation(libs.kairo.dependencyInjectionTesting)
  testImplementation(libs.kairo.testing)
}
