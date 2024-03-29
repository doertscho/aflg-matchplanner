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
//val orToolsVersion = "9.4.1874"

dependencies {
    implementation("net.java.dev.jna", "jna-platform", "5.5.0")
//    implementation(files("lib/ortools-java-${orToolsVersion}.jar"))
//    implementation(files("lib/ortools-win32-x86-64-${orToolsVersion}.jar"))
    implementation("com.google.ortools", "ortools-java", "8.2.9025")
    implementation("com.google.protobuf", "protobuf-java", "3.13.0")
    testImplementation(kotlin("test-junit"))
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}