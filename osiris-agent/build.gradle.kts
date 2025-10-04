plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  api(project(":osiris-core"))
  api(project(":osiris-schema"))

  implementation(libs.kairo.logging)
  implementation(libs.kairo.reflect)
  implementation(libs.kairo.serialization)
  implementation(libs.kairo.util)
  api(libs.ktor.utils)
  implementation(libs.serialization.json)
}
