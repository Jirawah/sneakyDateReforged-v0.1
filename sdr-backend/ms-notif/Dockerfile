# ====== Étape 1 : Build Maven ======
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache friendly : télécharge les deps d'abord
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Copie du code et build
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# ====== Étape 2 : Runtime (Debian/Ubuntu) ======
FROM eclipse-temurin:17-jdk-jammy
ENV TZ=Europe/Paris
WORKDIR /app

# Certificats & outils réseau de base (HTTPS/diagnostics)
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl ca-certificates \
 && update-ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# Jar
# (si le nom change, adapte le motif ci-dessous)
COPY --from=builder /app/target/ms-notif-*.jar app.jar

# Port exposé (ms-notif = 8086)
EXPOSE 8086

# Laisser le temps au Config Server / Eureka (comme ms-friend)
ENTRYPOINT ["sh", "-c", "sleep 30 && java -jar app.jar"]
