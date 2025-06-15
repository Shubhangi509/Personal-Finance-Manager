# Build stage
FROM eclipse-temurin:17-jdk-jammy as builder

# Set working directory
WORKDIR /app

# Copy the source code
COPY . .

# Make gradlew executable and build the application
RUN chmod +x ./gradlew && ./gradlew build -x test

# Run stage
FROM eclipse-temurin:17-jre-jammy

# Create a non-root user to run the application
RUN groupadd -r spring && useradd -r -g spring spring

# Set the working directory
WORKDIR /app

# Copy the JAR file from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set ownership of the JAR file
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"

# Expose the application port
EXPOSE 8080

# Run the application with optimized JVM settings
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
