# Base image with Java 17 runtime
FROM eclipse-temurin:17-jre

# Working directory inside the container
WORKDIR /app

# Copy the jar built by Maven
COPY target/food-ordering-system-1.0.0.jar app.jar

# Application port
EXPOSE 8080

# Start command
ENTRYPOINT ["java", "-jar", "app.jar"]