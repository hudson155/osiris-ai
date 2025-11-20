plugins {
  id("osiris-platform")
  id("osiris-platform-publish")
}

dependencies {
  /**
   * Automatically include all other modules.
   */
  constraints {
    rootProject.subprojects.forEach { subproject ->
      if (subproject.name == project.name) return@forEach
      evaluationDependsOn(subproject.path)
      if (!subproject.plugins.hasPlugin("maven-publish")) return@forEach
      subproject.publishing.publications.withType<MavenPublication> {
        api("$groupId:$artifactId:$version")
      }
    }
  }

  // flexmark
  // https://github.com/vsch/flexmark-java/tags
  val flexmarkVersion = "0.64.8"
  constraints.api("com.vladsch.flexmark:flexmark-all:$flexmarkVersion")

  // langchain4j
  // https://github.com/langchain4j/langchain4j/releases
  api(platform("dev.langchain4j:langchain4j-bom:1.8.0"))
}
