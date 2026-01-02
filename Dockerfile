# Build stage
FROM gradle:9-jdk25 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon
LABEL org.name="FinanceMaster"
FROM eclipse-temurin:25-jdk-jammy
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Create startup script that adds port to DATABASE_URL
RUN echo '#!/bin/sh\n\
# Convert postgresql://user:pass@host/db to jdbc:postgresql://user:pass@host:5432/db\n\
if [ -n "$DATABASE_URL" ]; then\n\
  # Check if port is already in URL\n\
  if echo "$DATABASE_URL" | grep -q "@.*:[0-9]"; then\n\
    # Port exists, just prepend jdbc:\n\
    export JDBC_DATABASE_URL="jdbc:$DATABASE_URL"\n\
  else\n\
    # No port, inject :5432 after hostname\n\
    # Replace @host/ with @host:5432/\n\
    MODIFIED_URL=$(echo "$DATABASE_URL" | sed "s|@\\([^/]*\\)/|@\\1:5432/|")\n\
    export JDBC_DATABASE_URL="jdbc:$MODIFIED_URL"\n\
  fi\n\
  export SPRING_DATASOURCE_URL="$JDBC_DATABASE_URL"\n\
fi\n\
exec java -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -Dspring.profiles.active=prod -jar /app.jar\n\
' > /start.sh && chmod +x /start.sh

ENTRYPOINT ["/start.sh"]
