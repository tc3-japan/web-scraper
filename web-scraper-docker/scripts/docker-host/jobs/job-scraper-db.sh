#!/usr/bin/env bash

DOCKER_DB_ID=`docker ps | grep scraper-db | awk '{print $1}'`

echo "docker exec ${DOCKER_DB_ID} /root/mysql/jobs.d/mysql_backup.sh"
docker exec ${DOCKER_DB_ID} /root/mysql/jobs.d/mysql_backup.sh
