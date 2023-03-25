FROM openjdk:17-alpine
COPY ../target/auth-service.jar auth-service.jar
#ADD ../target/auth-service.jar auth-service.jar
ENTRYPOINT ["java", "-jar", "auth-service.jar"]

#ARG JAR_FILE=*.jar
#COPY ${JAR_FILE} application.jar
#EXPOSE 8080