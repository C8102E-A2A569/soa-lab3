FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# curl не ставим — при сборке часто TLS-ошибка при apk (Windows/сеть). Healthcheck в docker-compose использует wget (BusyBox).

COPY --from=build /app/target/*.jar app.jar

COPY src/main/resources/keystore-service1.p12 /app/keystore-service1.p12

EXPOSE 18443 18543

ENTRYPOINT ["java", "-jar", "app.jar"]