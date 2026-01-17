plugins {
  id("osiris-library")
  id("osiris-library-publish")
  id("osiris-ksp")
}

dependencies {
  api(project(":osiris-agent"))

  implementation(libs.kairo.logging)
  api(libs.kairo.serialization)
  api(libs.langchain)
  api(libs.langchain.openAi)

  testImplementation(project(":osiris-evaluator"))
  testImplementation(project(":osiris-open-ai"))
  testImplementation(project(":osiris-schema"))

  testImplementation(libs.kairo.dependencyInjectionTesting)
  testImplementation(libs.kairo.testing)
}
