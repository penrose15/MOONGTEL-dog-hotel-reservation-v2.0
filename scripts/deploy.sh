#!/bin/bash

BUILD_JAR=$(ls /home/ubuntu/jenkins/server2/build/libs/server-0.0.1-SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_JAR)

echo "chmod +x /home/jenkins"
echo "hello"

sudo chmod 777 /home/jenkins


echo "> 현재 시간: $(date)" >> /home/ubuntu/jenkins/deploy.log

echo "> build filename: $JAR_NAME" >> /home/ubuntu/jenkins/deploy.log

echo "> build 파일 복사" >> /home/ubuntu/jenkins/deploy.log

DEPLOY_PATH=/home/ubuntu/jenkins

cp $BUILD_JAR /home/ubuntu/jenkins

echo "> 현재 실행중인 애플리케이션 pid 확인" >> /home/jenkins/deploy.log

CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ubuntu/jenkins/deploy.log
else
  echo "> kill -9 $CURRENT_PID" >> /home/ubuntu/jenkins/deploy.log
  sudo kill -9 $CURRENT_PID
  sleep 5
fi


DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포"    >> /home/ubuntu/jenkins/deploy.log
sudo nohup java -jar $DEPLOY_JAR >> /home/ubuntu/jenkins/deploy.log 2>/home/ubuntu/jenkins/deploy_err.log &