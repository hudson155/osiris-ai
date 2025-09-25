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

  // kairo
  // https://github.com/hudson155/kairo/releases
  api(platform("software.airborne.kairo:bom-full:6.0.0-alpha.1"))

  // langchain4j
  // https://github.com/langchain4j/langchain4j/releases
  api(platform("dev.langchain4j:langchain4j-bom:1.5.0"))
}
