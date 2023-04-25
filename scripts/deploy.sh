#!/bin/bash


sudo docker ps -a -q --filter "name=hsj" | grep -q . && docker stop hsj && docker rm hsj | true

sudo docker ps -a -q --filter "name=myredis" | grep -q . && docker stop myredis && docker rm myredis | true



HEALTH_CHECK_URL="http://127.0.0.1/test"

blue_port=8080
green_port=8081

function find_idle_profile() {
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" ${HEALTH_CHECK_URL})
    echo "> ${HTTP_STATUS}"

      if [ ${HTTP_STATUS} -ge 400 ]

      then
        CURRENT_PROFILE=real2

      else
        CURRENT_PROFILE=$(curl -s http://localhost/test)

      fi

      if [ ${CURRENT_PROFILE} == real1 ]
      then
        IDLE_PROFILE=real2
      else
        IDLE_PROFILE=real1
      fi

      echo "> ${IDLE_PROFILE}"

}

function find_idle_port() {
    IDLE_PROFILE=$(find_idle_profile)

    if [ ${IDLE_PROFILE} == real1 ]
    then
      echo "8080"
      y="real1"
    else
      echo "8081"
      y="real2"
    fi
}

IDLE_PORT=$(find_idle_port)
echo "${y}"

echo "> $IDLE_PORT에서 구동중인 어플리케이션 PID 확인"
IDLE_PID=$(sudo lsof -ti tcp:${IDLE_PORT})
echo "> ${IDLE_PID}"

if [ -z ${IDLE_PID} ]
then
  echo "> 현재 구동중인 어플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 `sudo lsof -i:8081 | grep docker-pr | grep -v grep | awk '{print $2}'`"
  sudo kill -15 `sudo lsof -i:8081 | grep docker-pr | grep -v grep | awk '{print $2}'`
  sleep 5
fi

# 기존 이미지 삭제
echo "sudo docker rmi admin1125/hsj:1"
sudo docker rmi admin1125/hsj:1

docker build --build-arg YML=${IDLE_PROFILE} admin1125/hsj:1 .

docker run -d --name myredis -p 6379:6379 redis


echo "docker run -d -p ${IDLE_PORT}:8080 --name hsj --rm admin1125/hsj:1.0"
docker run -d -p ${IDLE_PORT}:8080 --rm --name hsj admin1125/hsj:1.0

docker rmi admin1125/hsj:1

echo ">health check start"
echo "IDLE_PORT: $IDLE_PORT"
echo "> curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:$IDLE_PORT/test"
sleep 10

for RETRY_COUNT in {1..10}

do
  RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:$IDLE_PORT/test)
  echo " > ${RESPONSE}"

  if [ -n "${RESPONSE}" ]
  then
    echo "> health check success"
    switch_proxy
    break
  else
    echo "> health check fail"
    echo "health check: ${RESPONSE}"
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo "> health  check fail"
    echo "> 엔진엑스에 연결하지 않고 배포 종료"
    exit 1
  fi

  echo ">health check fail, wait 5 sec..."
  sleep 5

done

echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc
sudo service nginx reload
echo "Switch the reverse proxy direction of nginx to localhost 🔄"



docker rmi -f $(docker images -f "dangling=true" -q) || true