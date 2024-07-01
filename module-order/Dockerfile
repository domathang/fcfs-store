FROM amazoncorretto:21
ARG JAR_FILE=./module-order/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]