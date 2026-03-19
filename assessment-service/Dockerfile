# syntax=docker/dockerfile:1.7
FROM eclipse-temurin:17-jre

# Options JVM adaptées aux containers
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50"

# Répertoire d'exécution
WORKDIR /app

# Copie le JAR Spring Boot repackage (généré par maven package)
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Sécurité: exécuter en non-root
USER 1000

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]