# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
COPY newrelic.jar /app/
COPY newrelic.yml /app/
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/app/newrelic.jar", "-jar", "app.jar"]