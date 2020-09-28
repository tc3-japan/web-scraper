FROM mysql:latest

RUN apt-get update && apt-get install -y \
    binutils \
    iputils-ping

RUN apt update && apt install -y \
    procps \
    iproute2 \
    vim

RUN mkdir -p -m 777 /var/log/mysql && touch /var/log/mysql/mysqld.log && chmod 777 /var/log/mysql/mysqld.log

RUN mkdir /root/scraper

ADD ./conf.d /etc/mysql/conf.d
ADD ./initdb.d /root/mysql/initdb.d
ADD ./jobs.d /root/mysql/jobs.d

RUN chmod 777 /var/lib/mysql
RUN mkdir -p -m 777 /root/mysql/backup
