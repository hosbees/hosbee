FROM openjdk:17-jre-slim

WORKDIR /app

COPY hosbee-admin-ui/build/libs/*.jar app.jar

EXPOSE 9030

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]