FROM openjdk:11-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENV YML="real1"

EXPOSE 8080
EXPOSE 8081

RUN echo "run jar"
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=classpath:${YML}","/app.jar" ]
