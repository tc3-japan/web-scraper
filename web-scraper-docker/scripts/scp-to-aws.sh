#!/usr/bin/env bash

if [ $# -gt 0 ]; then
  PEM=${1}
  HOST=${2}
  echo "<pem_path> is ${PEM}"
  echo "<aws_host> is ${HOST}"
else
  echo "Usage: ./scp-to-aws.sh <pem_path> <aws_host>"
  exit 1
fi

DOCKER_DIR=`dirname $0`/..

# change owner of ec2-user docker directory
echo "ssh -i ${PEM} ec2-user@${HOST} sudo chown -R ec2-user:ec2-user docker"
ssh -i ${PEM} ec2-user@${HOST} sudo chown -R ec2-user:ec2-user docker

# scraper-web
echo "scp -r -i ${PEM} ${DOCKER_DIR}/nginx/build/libs     ec2-user@${HOST}:/home/ec2-user/docker/nginx/"
scp -r -i ${PEM} ${DOCKER_DIR}/nginx/build/libs     ec2-user@${HOST}:/home/ec2-user/docker/nginx/

# scraper-db
echo "scp -r -i ${PEM} ${DOCKER_DIR}/mysql/build/conf.d   ec2-user@${HOST}:/home/ec2-user/docker/mysql/"
scp -r -i ${PEM} ${DOCKER_DIR}/mysql/build/conf.d   ec2-user@${HOST}:/home/ec2-user/docker/mysql/
echo "scp -r -i ${PEM} ${DOCKER_DIR}/mysql/build/initdb.d ec2-user@${HOST}:/home/ec2-user/docker/mysql/"
scp -r -i ${PEM} ${DOCKER_DIR}/mysql/build/initdb.d ec2-user@${HOST}:/home/ec2-user/docker/mysql/
echo "scp -r -i ${PEM} ${DOCKER_DIR}/mysql/build/jobs.d   ec2-user@${HOST}:/home/ec2-user/docker/mysql/"
scp -r -i ${PEM} ${DOCKER_DIR}/mysql/build/jobs.d   ec2-user@${HOST}:/home/ec2-user/docker/mysql/

# jobs
echo "ssh -i ${PEM} ec2-user@${HOST} mkdir /home/ec2-user/jobs"
ssh -i ${PEM} ec2-user@${HOST} mkdir /home/ec2-user/jobs

echo "scp -r -i ${PEM} ${DOCKER_DIR}/scripts/docker-host/jobs   ec2-user@${HOST}:/home/ec2-user/"
scp -r -i ${PEM} ${DOCKER_DIR}/scripts/docker-host/jobs   ec2-user@${HOST}:/home/ec2-user/

echo "scp -i ${PEM} ${DOCKER_DIR}/scripts/docker-host/cron.d/docker-jobs ec2-user@${HOST}:/home/ec2-user/"
scp -i ${PEM} ${DOCKER_DIR}/scripts/docker-host/cron.d/docker-jobs ec2-user@${HOST}:/home/ec2-user/

echo "ssh -i ${PEM} ec2-user@${HOST} 'sudo mv /home/ec2-user/docker-jobs /etc/cron.d/; sudo chown root:root /etc/cron.d/docker-jobs'"
ssh -i ${PEM} ec2-user@${HOST} 'sudo mv /home/ec2-user/docker-jobs /etc/cron.d/; sudo chown root:root /etc/cron.d/docker-jobs'

