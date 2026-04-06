FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY target/securebank-backend-1.0-SNAPSHOT.jar app.jar
COPY target/dependency/ dependency/

EXPOSE 8080

# Include dependency folder in classpath!
CMD ["java", "-cp", "app.jar:dependency/*", "com.banking.BankingApp"]