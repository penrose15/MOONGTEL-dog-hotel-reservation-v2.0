#!/bin/bash

blue_port=8080
green_port=8081

ls

if curl -s "http://localhost:${blue_port}" > /dev/null # 서버가 살아있으면
then
    deployment_target=${blue_port}
		real="application-real1.yml"
else
    deployment_target=${green_port}
		real="application.real2.yml"
fi

cp reservation-0.0.1-SNAPSHOT.jar /home/ec2-user/reservation
nohup java -jar -Dspring.config.location=classpath:/${real}, /home/ec2-user/app/application-db.yml /home/ec2-user/reservation/reservation-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

for retry_count in $(seq 10)
do
  if curl -s "http://localhost:${deployment_target}" > /dev/null
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