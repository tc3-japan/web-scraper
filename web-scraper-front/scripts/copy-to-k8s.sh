#!/usr/bin/env bash

SERVER_HOST_PORT_ORG="127.0.0.1:8085"
SERVER_HOST_PORT_NEW=${SERVER_HOST_PORT_ORG}
if [ $# -gt 0 ]; then
  SERVER_HOST_PORT_NEW=${1}
fi
echo "<API Server's Host:Port> is ${SERVER_HOST_PORT_NEW}"

FRONT_BASE=$(dirname $0)/..
DOCKER_DIR=${FRONT_BASE}/../web-scraper-k8s

cp_code() {
  RELEASE_DIR=${DOCKER_DIR}/nginx/$1/libs

  rm -r ${RELEASE_DIR}/*

  echo "cp -ipr ${FRONT_BASE}/dist/$1/* ${RELEASE_DIR}/"
  cp -ipr ${FRONT_BASE}/dist/$1/* ${RELEASE_DIR}/

  if [ ${SERVER_HOST_PORT_ORG} = ${SERVER_HOST_PORT_NEW} ]; then
    return 0
  fi

  for i in ${RELEASE_DIR}/js/*; do
    echo $i
    echo "sed -i '' \"s/${SERVER_HOST_PORT_ORG}/${SERVER_HOST_PORT_NEW}/g\" $i"
    sed -i '' "s/${SERVER_HOST_PORT_ORG}/${SERVER_HOST_PORT_NEW}/g" $i
  done
}

cp_code admin
cp_code user
