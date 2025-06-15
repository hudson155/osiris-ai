plugins {
  id("osiris-ai")
  id("osiris-ai-publish")
}

dependencies {
  implementation(project(":osiris-agentic"))
  implementation(project(":osiris-core"))
  implementation(project(":osiris-langfuse"))
  implementation(project(":osiris-tracing"))
}
