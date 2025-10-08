# Use an official OpenJDK 17 image
FROM eclipse-temurin:17-jdk AS build

# Set working directory inside container
WORKDIR /app

# Copy project files
COPY . .

# Build the JAR using Maven Wrapper
RUN ./mvnw clean package -DskipTests

# ====================================================
# Stage 2: Run the built JAR
# ====================================================
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy JAR from previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the dynamic Render port
ENV PORT=8080
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
