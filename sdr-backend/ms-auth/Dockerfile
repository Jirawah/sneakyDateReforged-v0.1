# Étape de build avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape de runtime avec JDK léger et installation des certificats
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 🔐 Installation des certificats racines pour la JVM
RUN apt-get update && apt-get install -y curl ca-certificates && update-ca-certificates

# 🔧 Copie de l'application
COPY --from=builder /app/target/*.jar app.jar

# 🚀 Lancement (avec pause pour attendre Config Server)
ENTRYPOINT ["sh", "-c", "sleep 30 && java -jar app.jar"]
