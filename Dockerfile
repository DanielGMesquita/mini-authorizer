# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-jre

# Set the working directory in the container
WORKDIR /app

# Copy the built jar from the host to the container
COPY target/miniauthorizer-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
