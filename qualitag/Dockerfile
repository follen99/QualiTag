FROM ubuntu:latest
LABEL authors="mattia"

ENTRYPOINT ["top", "-b"]

# Use an official OpenJDK 21 runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the build.gradle and settings.gradle files
COPY build.gradle settings.gradle ./

# Copy the gradle wrapper files
COPY gradlew gradlew.bat ./
COPY gradle ./gradle

# Ensure the gradlew script is executable
RUN chmod +x gradlew

# Copy the source code
COPY src ./src

# Copy the scripts directory
COPY scripts ./scripts

# Copy the config directory
COPY config ./config

# Run the gradle build to download dependencies and build the project
RUN ./gradlew build

# Expose the port the application runs on
EXPOSE 8080 8443

# Define the command to run the application
CMD ["java", "-jar", "build/libs/qualitag-0.0.1-SNAPSHOT.jar"]