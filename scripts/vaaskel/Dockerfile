# --- Build stage: build the Spring Boot / Vaadin application JAR ---
FROM maven:3.9-eclipse-temurin-25 AS build

# Optional Maven JVM speed-up (faster startup for short-lived CI builds)
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

WORKDIR /build

# Copy Maven descriptor first to leverage Docker layer caching
COPY pom.xml .
# If you later add modules, copy their pom.xml files here as well
# COPY module-a/pom.xml module-a/pom.xml
# COPY module-b/pom.xml module-b/pom.xml

# Copy the actual source code
COPY src ./src

# Build the production JAR (includes Vaadin production frontend build)
RUN mvn -B -Pproduction clean package


# --- Runtime stage: run the built JAR on a minimal JRE image ---
FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /build/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
