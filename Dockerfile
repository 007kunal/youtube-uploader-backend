# Use official OpenJDK image
FROM eclipse-temurin:17-jdk

# Set working directory in the container
WORKDIR /app

# Copy everything from your local project into the container
COPY . .

# Give execute permission to mvnw (required!)
RUN chmod +x mvnw

# Build the project using Maven wrapper
RUN ./mvnw clean install

# Expose the port your Spring Boot app runs on
EXPOSE 8989

# Run the app
CMD ["java", "-jar", "target/youtube-uploader-backend-0.0.1-SNAPSHOT.jar"]
