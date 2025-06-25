Docker faster

Pour des builds plus rapides, surtout en développement, tirer parti du cache Docker en séparant le COPY du pom.xml et des sources :

dockerfile

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src





