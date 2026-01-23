plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  implementation(libs.flexmark)
  compileOnly(libs.kairo.serialization)

  testImplementation(libs.kairo.serialization)
  testImplementation(libs.kairo.testing)
}
