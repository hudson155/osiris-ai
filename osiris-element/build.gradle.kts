plugins {
  kotlin("plugin.serialization")
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  implementation(libs.flexmark)
  implementation(libs.kairo.serialization)

  testImplementation(libs.kairo.testing)
  testImplementation(libs.serialization.json)
}
