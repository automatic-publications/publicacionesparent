# Imagen base con Java 17
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el jar generado (ajusta el nombre si cambia la versión)
COPY target/userauth-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto de Spring Boot
EXPOSE 8081

# Comando para ejecutar el JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
