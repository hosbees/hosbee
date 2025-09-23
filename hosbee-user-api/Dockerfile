FROM openjdk:17-jre-slim

WORKDIR /app

COPY hosbee-user-api/build/libs/*.jar app.jar

EXPOSE 9092

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]