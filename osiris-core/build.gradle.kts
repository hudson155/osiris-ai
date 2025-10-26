plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  implementation(libs.kairo.coroutines)
  api(libs.kairo.serialization)
  api(libs.langchain.core)
  api(libs.serialization.json)
}
