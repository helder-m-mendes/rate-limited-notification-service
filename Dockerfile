# Stage 1: Build the application
FROM openjdk:22-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts

# Download dependencies
RUN ./gradlew dependencies

# Copy the rest of the application source code
COPY src src

# Build the application
RUN ./gradlew build

# Stage 2: Create the runtime image
FROM openjdk:22-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar /app/your-application.jar

# Run the application
CMD ["java", "-jar", "your-application.jar"]