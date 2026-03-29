# syntax=docker/dockerfile:1
FROM amazoncorretto:11 AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests -q


FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
#ENTRYPOINT ["sh", "-c", "printenv && java -jar app.jar"]
#ENTRYPOINT ["java", "-Xmx256m", "-Xms64m", "-Duser.timezone=America/Argentina/Buenos_Aires", "-jar", "app.jar"]
ENTRYPOINT ["java", "-Xmx384m", "-Xms64m", "-XX:+UseSerialGC", "-XX:MaxMetaspaceSize=128m", "-Duser.timezone=America/Argentina/Buenos_Aires", "-jar", "app.jar"
 



