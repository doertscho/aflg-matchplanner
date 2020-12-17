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

dependencies {
    implementation("net.java.dev.jna", "jna-platform", "5.5.0")
    implementation(files("lib/ortools-java-8.0.8283.jar"))
    implementation(files("lib/ortools-darwin-8.0.8283.jar"))
    implementation("com.google.protobuf", "protobuf-java", "3.13.0")
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
