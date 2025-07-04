# === Stage 1: Build the jar ===
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven files first for caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

# Build the jar
RUN ./mvnw clean package -DskipTests

# === Stage 2: Run the jar ===
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
