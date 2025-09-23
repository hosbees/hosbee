FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY hosbee-web-ui/build/libs/*.jar app.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]