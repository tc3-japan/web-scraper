#!/usr/bin/env bash

DOCKER_APP_ID=`docker ps | grep scraper-app | awk '{print $1}'`

echo "docker exec ${DOCKER_APP_ID} java -jar /root/scraper/web-scraper-server*.jar --batch=purchase_history"
docker exec ${DOCKER_APP_ID} java -jar /root/scraper/web-scraper-server*.jar --batch=purchase_history

echo "docker exec ${DOCKER_APP_ID} java -jar /root/scraper/web-scraper-server*.jar --batch=product"
docker exec ${DOCKER_APP_ID} java -jar /root/scraper/web-scraper-server*.jar --batch=product

echo "docker exec ${DOCKER_APP_ID} java -jar /root/scraper/web-scraper-server*.jar --batch=change_detection_check"
docker exec ${DOCKER_APP_ID} java -jar /root/scraper/web-scraper-server*.jar --batch=change_detection_check
