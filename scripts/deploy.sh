#!/bin/bash


sudo docker ps -a -q --filter "name=hsj" | grep -q . && docker stop hsj && docker rm hsj | true

sudo docker ps -a -q --filter "name=myredis" | grep -q . && docker stop myredis && docker rm myredis | true

# 기존 이미지 삭제
echo "sudo docker rmi admin1125/hsj:1.0"
sudo docker rmi admin1125/hsj:1.0

# 도커허브 이미지 pull
echo "sudo docker pull admin1125/hsj:1.0"
sudo docker pull admin1125/hsj:1.0

blue_port=8080
green_port=8081

if curl -s "http://localhost:${blue_port}" > /dev/null # 서버가 살아있으면
then
    deployment_target=${green_port}
else
    deployment_target=${blue_port}
fi

docker run -d --name myredis -p 6379:6379 redis

HEALTH_CHECK_URL="http://localhost/profile"

function find_idle_prifile() {
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" ${HEALTH_CHECK_URL})

      if [ ${HTTP_STATUS} -ge 400 ]

      then
        CURRENT_PROFILE=real2

      else
        CURRENT_PROFILE=$(curl -s http://localhost/profile)

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
    IDLE_PROFILE=$(find_idle_prifile)

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

echo "docker run -e YML=${y} --name hsj admin1125/hsj:1.0"
docker run -d -p 8080:${deployment_target} -e YML=${y} --name hsj admin1125/hsj:1.0

echo ">health check start"
echo "IDLE_PORT: $IDLE_PORT"
echo "> curl -s http://127.0.0.1:$IDLE_PORT/profile"
sleep 10

curl -s http://127.0.0.1:${IDLE_PORT}/profile
curl -s http://moongtel.shop:${IDLE_PORT}/profile

for RETRY_COUNT in {1..10}

do
  RESPONSE=$(curl -s http://moongtel.shop:${IDLE_PORT}/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)

  if [ ${UP_COUNT} -ge 1 ]
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


echo "set \$service_url http://127.0.0.1:${deployment_target};" > /etc/nginx/conf.d/service-url.inc
sudo service nginx reload
echo "Switch the reverse proxy direction of nginx to localhost 🔄"

if [ "${deployment_target}" == "${blue_port}" ]
then
    fuser -s -k ${green_port}/tcp
else
    fuser -s -k ${blue_port}/tcp
fi
echo "Kill the process on the opposite server."

docker rmi -f $(docker images -f "dangling=true" -q) || true