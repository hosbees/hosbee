FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY hosbee-admin-api/build/libs/*.jar app.jar

EXPOSE 9031

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]