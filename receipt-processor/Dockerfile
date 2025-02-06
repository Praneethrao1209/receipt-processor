# Use an official OpenJDK runtime as the base image
FROM eclipse-temurin:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file (make sure your project builds it in 'target' or 'build/libs')
COPY target/receipt-processor.jar app.jar

# Expose the application port (change if needed)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
