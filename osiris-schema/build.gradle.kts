plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  implementation(libs.kairo.datetime)
  implementation(libs.kairo.reflect)
  implementation(libs.kairo.util)
  compileOnly(libs.langchain4j) // Forced peer dependency.

  testImplementation(libs.kairo.testing)
  testImplementation(libs.langchain4j)
}
