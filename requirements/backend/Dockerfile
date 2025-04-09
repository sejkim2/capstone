# Step 1: Gradle을 사용하여 JAR 파일을 빌드
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY app/ .
RUN gradle clean build -x test

# Step 2: JAR 파일을 포함하여 실행할 컨테이너 생성
FROM openjdk:17-jdk-slim
WORKDIR /app

# wait-for-it.sh 다운로드 및 권한 설정
RUN apt-get update && apt-get install -y curl \
    && curl -o wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
    && chmod +x wait-for-it.sh \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# wait-for-it 실행 후 Spring Boot 앱 시작
ENTRYPOINT ["./wait-for-it.sh", "database:3306", "--", "java", "-jar", "app.jar"]
