# step1: build JAR file to Gradle
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY app/ .
RUN gradle clean build -x test

# step2: JAR 파일만 포함하여 실행할 컨테이너 생성
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

