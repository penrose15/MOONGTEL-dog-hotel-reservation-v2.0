#!/bin/bash


sudo docker ps -a -q --filter "name=hsj" | grep -q . && docker stop hsj && docker rm hsj | true

sudo docker ps -a -q --filter "name=myredis" | grep -q . && docker stop myredis && docker rm myredis | true

# 기존 이미지 삭제
echo "sudo docker rmi admin1125/hsj:1.0"
sudo docker rmi admin1125/hsj:1.0

# 도커허브 이미지 pull
echo "sudo docker pull admin1125/hsj:1.0"
sudo docker pull admin1125/hsj:1.0


docker run -d --name myredis -p 6379:6379 redis

HEALTH_CHECK_URL="http://localhost/test"

blue_port=8080
green_port=8081

function find_idle_profile() {
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" ${HEALTH_CHECK_URL})
    echo ${HTTP_STATUS}

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

      echo "${IDLE_PROFILE}"

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

echo "docker run -e YML=${y} --name hsj admin1125/hsj:1.0"
docker run -d -p ${IDLE_PORT}:${IDLE_PORT} -e YML=${y} --name hsj admin1125/hsj:1.0

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

sudo chmod +x /etc/nginx/conf.d/service-url.inc

echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" > /etc/nginx/conf.d/service-url.inc
sudo service nginx reload
echo "Switch the reverse proxy direction of nginx to localhost 🔄"



if [ "${IDLE_PORT}" == "${blue_port}" ]
then
    PID=$(netstat -lntp grep ${green_port} | grep | LISTEN)
    sudo kill -9 ${PID}
else
    PID=$(netstat -lntp grep ${blue_port} | grep | LISTEN)
    sudo kill -9 ${PID}
fi
echo "Kill the process on the opposite server."

docker rmi -f $(docker images -f "dangling=true" -q) || true