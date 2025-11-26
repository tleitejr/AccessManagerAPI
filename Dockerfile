# Etapa de build usando Maven + JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copiar fonte
COPY src ./src

# Build sem testes
RUN mvn -B clean package

# Etapa final: JDK 21 leve (alpine)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV TZ=America/Sao_Paulo

# instala curl (alpine) — necessário para o healthcheck do docker-compose
RUN apk add --no-cache curl

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=80", "-jar", "app.jar"]
