FROM gradle:8.10-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY src src
RUN gradle clean bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Create data directory for H2 database
RUN mkdir -p /app/data && chown -R appuser:appgroup /app

COPY --from=build /app/build/libs/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

# Create volume for H2 database persistence
VOLUME ["/app/data"]

EXPOSE 8080

# Set H2 database path and add JVM optimizations
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.datasource.url=jdbc:h2:file:/app/data/db"]