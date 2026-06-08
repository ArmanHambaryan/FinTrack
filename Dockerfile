FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY finance-tracker-mvc/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]