FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY hosbee-user-api/build/libs/*.jar app.jar

EXPOSE 9092

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]