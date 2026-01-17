plugins {
  java
  id("com.google.devtools.ksp")
}

dependencies {
  ksp(platform(Airborne.kairo))
  ksp("io.insert-koin:koin-ksp-compiler")
}
