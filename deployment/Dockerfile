#FROM eclipse-temurin:21-jdk-alpine
#VOLUME /tmp
#COPY *.jar crediya-ms-autenticacion.jar
#ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
# Replace with a non-root user to avoid running the container with excessive privileges
#USER appuser
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar crediya-ms-autenticacion.jar" ]

# ==============================
# Etapa 1: Compilación
# ==============================
FROM gradle:8.10-jdk21 AS builder
WORKDIR /workspace
COPY . .
RUN gradle clean :app-service:bootJar --no-daemon

# ==============================
# Etapa 2: Imagen final
# ==============================
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=builder /workspace/applications/app-service/build/libs/*.jar crediya-ms-autenticacion.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "/app/crediya-ms-autenticacion.jar"]

