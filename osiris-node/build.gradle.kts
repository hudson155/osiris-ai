plugins {
  id("osiris-library")
  id("osiris-library-publish")
}

dependencies {
  api(libs.ktorUtils) // For [Attributes].
}
