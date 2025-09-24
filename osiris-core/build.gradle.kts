plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  implementation(libs.kairo.coroutines)
  api(libs.langchain.core)
}
