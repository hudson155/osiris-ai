plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  compileOnly(project(":osiris-llm")) // Forced peer dependency.

  api(libs.kairo.protectedString)
  api(libs.langchain4j.openAi)
}
