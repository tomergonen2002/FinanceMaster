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

# Ensure the app binds to the Render-provided PORT and all interfaces
# using JVM system properties as a hard override, independent of Spring config.
# Falls back to 8080 locally if PORT is not set.
ENTRYPOINT ["sh","-c","exec java -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -jar /app.jar"]
