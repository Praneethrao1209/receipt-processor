# Stage 1: Build the Java application
FROM openjdk:17-jdk-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and configuration files
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Copy the application source code
COPY src ./src

# Copy the script used to execute mvnw
COPY mvnw mvnw

# Set execute permission on the Maven wrapper
RUN chmod +x mvnw

# Build the application using Maven
RUN ./mvnw clean package -DskipTests && ls -l target/

# Verify if the JAR was built successfully
RUN find target/ -type f && \
    if [ -f target/*.jar ]; then echo "Build successful"; else echo "Build failed" && exit 1; fi

# Stage 2: Create a minimal runtime image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port (change if needed)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]