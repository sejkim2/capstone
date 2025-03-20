# Step 1: Gradle을 사용하여 JAR 파일을 빌드
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY app/ .
RUN gradle clean build -x test

# Step 2: JAR 파일을 포함하여 실행할 컨테이너 생성
FROM openjdk:17-jdk-slim
WORKDIR /app

# .jar 파일을 명시적으로 경로를 지정하여 복사
# build.gradle에서 앱 이름 지정 가능 (default : 프로젝트 디렉토리명)
COPY --from=builder /app/build/libs/app-0.0.1-SNAPSHOT.jar /app/app.jar 

ENTRYPOINT ["java", "-jar", "app.jar"]
