FROM eclipse-temurin:21-jdk-alpine
ARG JAR_FILE=target/grpc-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 9090

ENTRYPOINT ["java","-jar","/app.jar"]