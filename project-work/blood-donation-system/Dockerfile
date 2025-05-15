FROM docker.io/bellsoft/liberica-openjdk-alpine:21
COPY ./build/libs/blood-donation-system-1.0-SNAPSHOT.jar /opt/blood-donation-system.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/opt/blood-donation-system.jar"]
