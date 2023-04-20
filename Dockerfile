FROM openjdk:11-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENV YML="real1"

EXPOSE 8080
EXPOSE 8081

RUN yum update && yum install -y sudo

RUN yum -y install psmisc

RUN adduser --disabled-password --gecos "" user  \
        && echo 'user:user' | chpasswd \
        && adduser user sudo \
        && echo 'user ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoer

RUN echo "run jar"
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=classpath:${YML}","/app.jar" ]
