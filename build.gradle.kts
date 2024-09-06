import kotlin.text.set

plugins {
    kotlin("jvm") version "2.0.20"
    application
    jacoco
    id("com.bmuschko.docker-remote-api") version "9.3.0"
}

group = "org"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    implementation("org.slf4j:slf4j-api:2.0.0")
    implementation("org.slf4j:slf4j-simple:2.0.0")
    implementation("jakarta.jms:jakarta.jms-api:3.1.0")
    implementation("com.amazonaws:amazon-sqs-java-messaging-lib:2.1.3")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

application {
    mainClass = "org.main.MainKt"
}

docker {
    url.set("unix:///var/run/docker.sock")
    registryCredentials {
        username.set(System.getenv("DOCKER_USERNAME"))
        password.set(System.getenv("DOCKER_PASSWORD"))
    }
}

tasks.register<com.bmuschko.gradle.docker.tasks.image.DockerBuildImage>("buildDockerImage") {
    inputDir.set(file("."))
    images.add("${project.group}/${project.name}:${project.version}")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22)) // Ensure this matches your installed Java version
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_22)
    }
}