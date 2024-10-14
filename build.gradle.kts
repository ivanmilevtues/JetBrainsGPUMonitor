import java.util.*

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.25"
  id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.strange.dr"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

// Configure Gradle IntelliJ Plugin
intellij {
  version.set("2023.2.6")
  type.set("IC") // Target IDE Platform

  plugins.set(listOf(/* Plugin Dependencies */))
}

dependencies {
  implementation("org.jocl:jocl:2.0.5")
  // Add other dependencies here if needed
}

// You can specify native libraries for each platform if needed
val osName = System.getProperty("os.name").lowercase(Locale.ENGLISH)
val arch = System.getProperty("os.arch").lowercase(Locale.ENGLISH)

// Determine the appropriate native library for the OS and architecture
val nativeLibs = when {
  osName.contains("win") && arch.contains("64") -> "libs/jocl.dll"
  osName.contains("win") -> "libs/jocl32.dll"
  osName.contains("mac") -> "libs/libjocl.dylib"
  osName.contains("nux") -> "libs/libjocl.so"
  else -> throw RuntimeException("Unsupported OS or architecture: $osName $arch")
}

// Load the native library
tasks.register<JavaExec>("runWithJocl") {
  classpath = sourceSets.main.get().runtimeClasspath
  mainClass.set("com.strange.dr.MainKt") // Replace with your main class
  jvmArgs = listOf("-Djava.library.path=$nativeLibs")
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }

  patchPluginXml {
    sinceBuild.set("232")
    untilBuild.set("242.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
