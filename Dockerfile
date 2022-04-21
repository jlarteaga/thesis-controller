FROM openjdk:11-jdk-slim AS builder
WORKDIR /app
COPY gradlew gradlew.bat settings.gradle build.gradle /app/
COPY src /app/src
COPY gradle /app/gradle
RUN ./gradlew build

FROM openjdk:11-jre-slim
RUN apt-get update && apt-get install -y curl
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/coordinator.jar
EXPOSE 3005
ENTRYPOINT ["java", "-jar", "/app/coordinator.jar"]