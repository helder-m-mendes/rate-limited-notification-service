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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.3.0")
    implementation("software.amazon.awssdk:sqs:2.17.89")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}


tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

application {
    mainClass = "org.MainKt"
}
