plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  api(project(":osiris-core"))
  implementation(project(":osiris-schema"))

  implementation(libs.kairo.reflect)
  implementation(libs.kairo.serialization)
  implementation(libs.kairo.util)
  implementation(libs.serialization.json)
}
