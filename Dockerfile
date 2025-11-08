# Build stage
FROM gradle:9-jdk25 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

LABEL org.name="FinanceMaster"

# Package stage
FROM eclipse-temurin:25-jdk-jammy
# Kopiere beliebiges Jar aus build/libs und nenne es app.jar
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
