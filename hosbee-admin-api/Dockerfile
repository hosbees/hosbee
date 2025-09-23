FROM openjdk:17-jre-slim

WORKDIR /app

COPY hosbee-admin-api/build/libs/*.jar app.jar

EXPOSE 9031

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]