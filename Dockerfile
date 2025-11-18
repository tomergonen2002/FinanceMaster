# Build stage
FROM gradle:9-jdk25 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon
LABEL org.name="FinanceMaster"
FROM eclipse-temurin:25-jdk-jammy
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["sh","-c","exec java -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -jar /app.jar"]
