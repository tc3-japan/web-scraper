FROM openjdk:8-jdk-slim

MAINTAINER tc3.co.jp

RUN apt-get update && apt-get install -y \
    binutils \
    iputils-ping

RUN apt update && apt install -y \
    procps \
    iproute2 \
    vim

RUN mkdir /root/scraper

COPY ./libs/web-scraper-server*.jar /root/scraper/
COPY ./libs/application.yaml  /root/scraper/application.yaml
