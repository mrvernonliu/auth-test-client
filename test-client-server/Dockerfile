FROM openjdk:11-jdk-slim

RUN mkdir -p /test-client-server
WORKDIR test-client-server

COPY /target .
CMD ["ls"]

EXPOSE 8080

CMD ["java", "-jar", "test-client-server-0.0.1.jar"]