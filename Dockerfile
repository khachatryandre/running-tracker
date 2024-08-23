FROM maven:latest AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/target/running-tracker-0.0.1-SNAPSHOT.jar running-tracker.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "running-tracker.jar"]