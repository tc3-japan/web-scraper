#!/usr/bin/env bash

SERVER_BASE=`dirname $0`/..
DOCKER_DIR=${SERVER_BASE}/../web-scraper-docker
MYSQL_INIT_DIR=${DOCKER_DIR}/mysql/build/initdb.d

echo "cp -ip ${SERVER_BASE}/docs/encrypt-table.sql ${MYSQL_INIT_DIR}"
cp -ip ${SERVER_BASE}/docs/encrypt-table.sql ${MYSQL_INIT_DIR}

# Sample Data
echo "cp -ip ${SERVER_BASE}/docs/test-data.sql ${MYSQL_INIT_DIR}"
cp -ip ${SERVER_BASE}/docs/test-data.sql ${MYSQL_INIT_DIR}
