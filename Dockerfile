# Build stage
FROM gradle:9-jdk25 AS build
WORKDIR /home/gradle/src

# Copy only dependency files first (for caching)
COPY --chown=gradle:gradle build.gradle settings.gradle ./
COPY --chown=gradle:gradle gradle ./gradle

# Download dependencies only (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY --chown=gradle:gradle . .

# Build the application
RUN gradle build --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jdk-jammy

# Force prod profile in container; tests/build stay on dev default
ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["sh","-c","exec java -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -jar /app.jar"]
