# ====== Étape 1 : Build Maven ======
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copie du projet et build. 
# NOTE: on skippe entièrement les tests pour éviter la compilation des tests tant qu'ils ne sont pas finalisés.
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# ====== Étape 2 : Runtime léger ======
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Certificats (utile pour appeler Config Server/Eureka en HTTPS si besoin)
RUN apt-get update && apt-get install -y curl ca-certificates && update-ca-certificates && rm -rf /var/lib/apt/lists/*

# JAR
COPY --from=builder /app/target/*.jar app.jar

# Port exposé (ms-rdv = 8088)
EXPOSE 8088

# Petit délai pour laisser le Config Server/Eureka devenir "UP"
ENTRYPOINT ["sh", "-c", "sleep 30 && java -jar app.jar"]
