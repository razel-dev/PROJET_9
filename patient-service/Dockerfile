FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY . .
RUN mvn -q -DskipTests -pl patient-service -am package spring-boot:repackage \
 && cp patient-service/target/*.jar /workspace/app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/app.jar /app/app.jar

USER 1000
EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
