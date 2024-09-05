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
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.3.0")
    implementation("software.amazon.awssdk:sqs:2.17.89")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.6")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

application {
    mainClass = "org.MainKt"
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