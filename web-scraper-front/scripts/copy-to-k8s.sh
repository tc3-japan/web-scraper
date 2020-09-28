#!/usr/bin/env bash

SERVER_HOST_PORT_ORG="127.0.0.1:8085"
SERVER_HOST_PORT_NEW=${SERVER_HOST_PORT_ORG}
if [ $# -gt 0 ]; then
  SERVER_HOST_PORT_NEW=${1}
fi
echo "<API Server's Host:Port> is ${SERVER_HOST_PORT_NEW}"

FRONT_BASE=`dirname $0`/..
DOCKER_DIR=${FRONT_BASE}/../web-scraper-k8s
RELEASE_DIR=${DOCKER_DIR}/nginx/libs

echo "cp -ipr ${FRONT_BASE}/dist/css ${RELEASE_DIR}/"
cp -ipr ${FRONT_BASE}/dist/css ${RELEASE_DIR}/

echo "mkdir ${RELEASE_DIR}/js"
mkdir ${RELEASE_DIR}/js
for file in `ls ${FRONT_BASE}/dist/js`; do
  echo sed "s/${SERVER_HOST_PORT_ORG}/${SERVER_HOST_PORT_NEW}/g ${FRONT_BASE}/dist/js/${file} > ${RELEASE_DIR}/js/`basename ${file}`"
  sed "s/${SERVER_HOST_PORT_ORG}/${SERVER_HOST_PORT_NEW}/g" ${FRONT_BASE}/dist/js/${file} > ${RELEASE_DIR}/js/`basename ${file}`
done

echo "cp -ip ${FRONT_BASE}/dist/index.html ${RELEASE_DIR}/"
cp -ip ${FRONT_BASE}/dist/index.html ${RELEASE_DIR}/

echo "cp -ip ${FRONT_BASE}/dist/favicon.ico ${RELEASE_DIR}/"
cp -ip ${FRONT_BASE}/dist/favicon.ico ${RELEASE_DIR}/
