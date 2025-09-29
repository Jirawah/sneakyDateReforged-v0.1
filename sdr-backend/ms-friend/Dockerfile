# ====== Étape 1 : Build Maven ======
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Option cache friendly (facultatif si tu utilises mvnw)
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Copie sources et build
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# ====== Étape 2 : Runtime (Debian/Ubuntu) ======
FROM eclipse-temurin:17-jdk-jammy
ENV TZ=Europe/Paris
WORKDIR /app

# Certificats & outils réseau de base (utile pour HTTPS/diagnostics)
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl ca-certificates \
 && update-ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# Jar
COPY --from=builder /app/target/ms-friend-*.jar app.jar

# Port exposé (ms-friend = 8083)
EXPOSE 8083

# Laisser le temps au Config Server / Eureka
ENTRYPOINT ["sh", "-c", "sleep 30 && java -jar app.jar"]
