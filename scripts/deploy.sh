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
    deployment_target=${blue_port}
    y="application-real1.yml"
else
    deployment_target=${green_port}
    y="application-real2.yml"
fi

echo "docker run -d -p ${deployment_target}:${deployment_target} --name hsj admin1125/hsj:1.0"
docker run -d -p ${deployment_target}:${deployment_target} --build-arg YML=${y} --name hsj admin1125/hsj:1.0

docker run -d --name myredis -p 6379:6379 redis

HEALTH_CHECK_URL="http://localhost:${deployment_target}/profile"
EXPECTED_STATUS_CODE=200

for retry_count in $(seq 10)

do
  http_status=$(curl -s -o /dev/null -w "%{http_code}" ${HEALTH_CHECK_URL})

  if [ ${http_status} -eq ${EXPECTED_STATUS_CODE} ]
  then
      echo "Health check success ✅"
      break
  fi

  if [ $retry_count -eq 10 ]
  then
    echo "Health check failed ❌"
    exit 1
  fi

	echo "The server is not alive yet. Retry health check in 10 seconds..."
	sleep 10
done

echo "set \$service_url http://localhost:${deployment_target};" > /etc/nginx/conf.d/service-url.inc
service nginx reload
echo "Switch the reverse proxy direction of nginx to localhost 🔄"

if [ "${deployment_target}" == "${blue_port}" ]
then
    fuser -s -k ${green_port}/tcp
else
    fuser -s -k ${blue_port}/tcp
fi
echo "Kill the process on the opposite server."

docker rmi -f $(docker images -f "dangling=true" -q) || true