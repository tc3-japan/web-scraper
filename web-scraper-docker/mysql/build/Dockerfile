FROM mysql:latest

RUN apt-get update && apt-get install -y \
    binutils \
    iputils-ping

RUN apt update && apt install -y \
    procps \
    iproute2 \
    vim

RUN mkdir -p /var/log/mysql && touch /var/log/mysql/mysqld.log

RUN mkdir /root/scraper
