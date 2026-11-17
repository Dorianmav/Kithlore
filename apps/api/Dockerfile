FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY apps/api/mvnw ./mvnw
COPY apps/api/.mvn/ ./.mvn/
COPY apps/api/pom.xml ./pom.xml
COPY apps/api/config/ ./config/
COPY apps/api/rules/pom.xml ./rules/pom.xml
COPY apps/api/app/pom.xml ./app/pom.xml
RUN chmod +x mvnw && \
    for attempt in 1 2 3; do \
      ./mvnw -B -pl app -am dependency:go-offline && exit 0; \
      echo "Maven dependency prefetch failed (attempt ${attempt}/3); retrying..." >&2; \
      sleep $((attempt * 10)); \
    done; \
    exit 1
COPY apps/api/app/src/ ./app/src/
RUN ./mvnw -B -pl app -am package -DskipTests
RUN cp app/target/app-*.jar application.jar && java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S kithlore && adduser -S -G kithlore kithlore
COPY --from=build --chown=kithlore:kithlore /workspace/extracted/dependencies/ ./
COPY --from=build --chown=kithlore:kithlore /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=kithlore:kithlore /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=kithlore:kithlore /workspace/extracted/application/ ./
USER kithlore
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx1g", "-jar", "application.jar"]
