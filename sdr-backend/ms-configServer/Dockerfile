FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl ca-certificates \
 && update-ca-certificates \
 && rm -rf /var/lib/apt/lists/*
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
