import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}

group = "de.kiel-koalas"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    flatDir {
        dirs("lib")
    }
}

val orToolsVersion = "8.1.8487"

dependencies {
    implementation("net.java.dev.jna", "jna-platform", "5.5.0")
    implementation(files("lib/ortools-java-${orToolsVersion}.jar"))
    implementation(files("lib/ortools-darwin-${orToolsVersion}.jar"))
    implementation("com.google.protobuf", "protobuf-java", "3.13.0")
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
