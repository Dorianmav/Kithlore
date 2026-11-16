FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY apps/api/mvnw apps/api/.mvn/ apps/api/pom.xml ./
COPY apps/api/config/ ./config/
COPY apps/api/rules/pom.xml ./rules/pom.xml
COPY apps/api/app/pom.xml ./app/pom.xml
RUN chmod +x mvnw && ./mvnw -B -pl app -am dependency:go-offline
COPY apps/api/rules/src/ ./rules/src/
COPY apps/api/app/src/ ./app/src/
RUN ./mvnw -B -pl app -am package -DskipTests
RUN java -Djarmode=layertools -jar app/target/app-*.jar extract --destination extracted

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache curl
WORKDIR /app
RUN addgroup -S kithlore && adduser -S -G kithlore kithlore
COPY --from=build --chown=kithlore:kithlore /workspace/extracted/dependencies/ ./
COPY --from=build --chown=kithlore:kithlore /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=kithlore:kithlore /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=kithlore:kithlore /workspace/extracted/application/ ./
USER kithlore
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=60.0", "org.springframework.boot.loader.launch.JarLauncher"]
