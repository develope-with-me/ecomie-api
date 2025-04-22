FROM maven:3.9.9-amazoncorretto-23-alpine AS build

WORKDIR /app

COPY ./pom.xml .

##RUN mvn clean install
RUN mvn dependency:go-offline

COPY ./src ./src
COPY ./.env .

RUN mvn clean package -DskipTests


FROM amazoncorretto:23-alpine AS runtime

# Set the working directory for the runtime container
WORKDIR /app
COPY ./src/main/resources ./src/main/resources

# Copy the packaged jar file from the previous build stage
COPY --from=build /app/target/ecomie.jar ./ecomie.jar

# Expose the application port
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "ecomie.jar"]
