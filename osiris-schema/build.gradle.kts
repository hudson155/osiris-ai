plugins {
  kotlin("plugin.serialization")
  id("osiris-library")
  id("osiris-library-publish")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.add("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
  }
}

dependencies {
  implementation(libs.kairo.serialization)
  implementation(libs.langchain)

  testImplementation(libs.kairo.testing)
}
