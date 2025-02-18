# Stage 1: Build the application
# Use an official OpenJDK 21 runtime as a parent image
FROM openjdk:21-jdk-slim AS build
LABEL authors="Mattia Marino"

# Install Firefox and necessary dependencies
RUN apt-get update && apt-get install -y firefox-esr

# Download GeckoDriver
RUN apt-get install -y wget
RUN wget https://github.com/mozilla/geckodriver/releases/download/v0.35.0/geckodriver-v0.35.0-linux64.tar.gz
RUN tar -xvzf geckodriver-v0.35.0-linux64.tar.gz
RUN mv geckodriver /usr/local/bin/
RUN chmod +x /usr/local/bin/geckodriver

# Set the GeckoDriver path (adjust version if needed)
ENV WEBDRIVER_GECKO_DRIVER=/usr/local/bin/geckodriver

# Set Firefox binary location (needed for Selenium)
ENV FIREFOX_BIN=/usr/bin/firefox

# Set the working directory in the container
WORKDIR /app

# Copy Gradle wrapper and configuration files
COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy the .env file
COPY .env .env

# Copy the source code
COPY src ./src

# Copy the config directory
COPY config ./config

# Ensure the gradlew script is executable
RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Run the gradle build to download dependencies and build the project
RUN ./gradlew build --no-daemon --info

# Stage 2: Runtime
FROM build

# Set working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/qualitag-0.0.1-SNAPSHOT.jar /app/qualitag.jar

# Expose the port the application runs on
EXPOSE 8080 8443

# Set the default command to run the application
ENTRYPOINT ["java", "-jar"]
CMD [ "qualitag.jar" ]