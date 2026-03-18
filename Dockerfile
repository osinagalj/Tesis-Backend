# syntax=docker/dockerfile:1
FROM amazoncorretto:11 AS build
ARG RAILWAY_CAHCE_KEY=default
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN --mount=type=cache,id=${RAILWAY_CAHCE_KEY},target=/root/.m2 ./mvnw dependency:go-offline
COPY src ./src
RUN --mount=type=cache,id=${RAILWAY_CAHCE_KEY},target=/root/.m2 ./mvnw package -DskipTests -q


FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
