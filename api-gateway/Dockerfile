FROM eclipse-temurin:17-jre
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50"
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
USER 1000
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]