# === Stage 1: Build ===
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

# ✅ Fix permission
RUN chmod +x mvnw

# Build the jar
RUN ./mvnw clean package -DskipTests

# === Stage 2: Run ===
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
