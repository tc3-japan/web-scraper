#!/usr/bin/env bash

SCRIPT_NAME=`basename $0`
SERVER_BASE=`dirname $0`/..

JAR_PATH="${SERVER_BASE}/build/libs/web-scraper-server-0.0.1.jar"
TACTIC_PATH="${SERVER_BASE}/scripts/tactic__load-test.yaml"
SQL_DIR="${SERVER_BASE}/docs"
LOG_PATH="${SERVER_BASE}/logs/${SCRIPT_NAME}_`date +%Y%m%d-%H%M%S`.log"

DB_NAME="web_scraper"
DB_USER="root"
DB_PASS="mypassword"


if [ $# -gt 0 ]; then
  LOOP_COUNT=${1}
else
  echo "Usage: ./${SCRIPT_NAME} <Loop Count>"
  exit 1
fi

function log() {
  echo "[$(date '+%Y-%m-%dT%H:%M:%S')] $1: $2"
}
set -u

# Log start
exec >> "${LOG_PATH}"
exec 2>&1

log "INFO" "Starting Load Test (Executing batch count=${LOOP_COUNT})"
for i in `seq 1 ${LOOP_COUNT}`; do
  log "INFO" "Load Test count: ${i}"

  log "INFO" "initialize database for load test"
  mysql --host 0.0.0.0 --port 3306 --user=${DB_USER} --password=${DB_PASS} ${DB_NAME} < ${SQL_DIR}/load-test__initialize-product.sql
  log "INFO" "check database, product table"
  mysql --host 0.0.0.0 --port 3306 --user root --password=mypassword web_scraper <<EOF
    select count(*) from product;
EOF

  log "INFO" "execute batch=product"
  java -DtacticFile=${TACTIC_PATH} -jar ${JAR_PATH} --batch=product --site=amazon

done

log "INFO" "Finished Load Test"

exit 0
