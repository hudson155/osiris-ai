plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(project(":osiris-langfuse"))
  implementation(project(":osiris-prompt"))
}
