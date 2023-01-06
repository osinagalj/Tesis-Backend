# For Java 8, try this
# FROM openjdk:8-jdk-alpine

# For Java 11, try this
FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app-tesis.jar
ENTRYPOINT ["java","-jar","app-tesis.jar"]

# Refer to Maven build -> finalName
#ARG JAR_FILE=target/spring-boot-web.jar

# cd /opt/app
# WORKDIR /src/app

# cp target/spring-boot-web.jar /opt/app/app.jar
#COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar