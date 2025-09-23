plugins {
  kotlin("plugin.serialization")
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  implementation(libs.kairo.serialization)
  implementation(libs.langchain.core)
}
