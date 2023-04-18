FROM openjdk:11-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENV YML="application-real1.yml"


RUN echo "run jar"
ENTRYPOINT ["java","-jar","--spring.config.location=/home/ec2-user/app/${YML},/home/ec2-user/app/application-db.yml","/app.jar" ]
