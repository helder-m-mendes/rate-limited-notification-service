plugins {
    kotlin("jvm") version "2.0.20"
    application
    jacoco
}

group = "org"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "org.MainKt"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}