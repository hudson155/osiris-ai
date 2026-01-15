plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  implementation(libs.kairo.datetime)
  implementation(libs.kairo.reflect)
  compileOnly(libs.langchain) // Forced peer dependency.

  testImplementation(libs.kairo.testing)
  testImplementation(libs.langchain)
}
