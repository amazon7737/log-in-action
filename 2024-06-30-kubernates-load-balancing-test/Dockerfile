FROM openjdk:17
COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=server", "app.jar"]