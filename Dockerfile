# Use an OpenJDK 17 image for building and running the app
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/MBank-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]