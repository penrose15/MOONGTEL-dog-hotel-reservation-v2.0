FROM openjdk:11-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar


RUN echo "run jar"
ENTRYPOINT ["java","-jar","--spring.config.location=/home/ec2-user/app/application.yml","/app.jar" ]
