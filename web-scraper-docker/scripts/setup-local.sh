#!/usr/bin/env bash

LOCAL_DOCKER_DIR=`dirname $0`/../local-data/

# create directory for docker
echo "mkdir -p ${LOCAL_DOCKER_DIR}/scraper/logs"
mkdir -p ${LOCAL_DOCKER_DIR}/scraper/logs

echo "mkdir -p ${LOCAL_DOCKER_DIR}/mysql/data"
mkdir -p ${LOCAL_DOCKER_DIR}/mysql/data

echo "mkdir -p ${LOCAL_DOCKER_DIR}/mysql/logs"
mkdir -p ${LOCAL_DOCKER_DIR}/mysql/logs
