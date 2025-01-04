FROM openjdk:17-alpine
WORKDIR /app
ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]