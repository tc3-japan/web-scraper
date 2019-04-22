#!/usr/bin/env bash

DOCKER_APP_ID=`docker ps | grep scraper-app | awk '{print $1}'`

echo "docker exec ${DOCKER_APP_ID} bash -c 'cd /root/scraper; java -jar web-scraper-server*.jar --batch=purchase_history --spring.config.location=file:application.yaml'"
docker exec ${DOCKER_APP_ID} bash -c 'cd /root/scraper; java -jar web-scraper-server*.jar --batch=purchase_history --spring.config.location=file:application.yaml'

echo "docker exec ${DOCKER_APP_ID} bash -c 'cd /root/scraper; java -jar web-scraper-server*.jar --batch=product --spring.config.location=file:application.yaml'"
docker exec ${DOCKER_APP_ID} bash -c 'cd /root/scraper; java -jar web-scraper-server*.jar --batch=product --spring.config.location=file:application.yaml'

echo "docker exec ${DOCKER_APP_ID} bash -c 'cd /root/scraper; java -jar web-scraper-server*.jar --batch=change_detection_check --spring.config.location=file:application.yaml'"
docker exec ${DOCKER_APP_ID} bash -c 'cd /root/scraper; java -jar web-scraper-server*.jar --batch=change_detection_check --spring.config.location=file:application.yaml'
