plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  implementation(libs.flexmark)
  compileOnly(libs.kairo.serialization) // Forced peer dependency.

  testImplementation(libs.kairo.serialization)
  testImplementation(libs.kairo.testing)
}
