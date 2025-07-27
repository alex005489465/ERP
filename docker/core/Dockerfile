# Stage 1: Build with Maven 3.9.11 + JDK 21
FROM maven:3.9.11-eclipse-temurin-21 AS builder

WORKDIR /app

COPY src/core/pom.xml src/core/mvnw ./
COPY src/core/.mvn .mvn

RUN ./mvnw dependency:go-offline

COPY src/core/src ./src

RUN ./mvnw clean package -DskipTests

# Stage 2: Run with lightweight JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/core-0.0.1-SNAPSHOT.jar app.jar

# 複製 Docker 專用的配置文件（從 docker/core 目錄）
COPY docker/core/application-docker.yml /app/application-docker.yml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]