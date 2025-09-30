# ====== Étape 1 : Build Maven ======
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache deps en amont
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Build
COPY . .
RUN mvn clean package -DskipTests

# ====== Étape 2 : Runtime ======
FROM eclipse-temurin:17-jdk-jammy
ENV TZ=Europe/Paris
WORKDIR /app

RUN apt-get update \
 && apt-get install -y --no-install-recommends curl ca-certificates \
 && update-ca-certificates \
 && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["sh", "-c", "sleep 30 && java -jar app.jar"]
