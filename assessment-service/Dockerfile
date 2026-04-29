FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY . .
RUN mvn -q -f /workspace/pom.xml -DskipTests -pl assessment-service -am package spring-boot:repackage \
 && JAR="$(find assessment-service/target -maxdepth 1 -type f -name '*.jar' ! -name '*plain.jar' | head -n1)" \
 && cp "$JAR" /workspace/app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/app.jar /app/app.jar

USER 1000
EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
