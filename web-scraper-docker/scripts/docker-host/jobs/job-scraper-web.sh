#!/usr/bin/env bash

DOCKER_WEB_ID=`docker ps | grep scraper-web | awk '{print $1}'`
