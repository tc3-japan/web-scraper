FROM mysql:latest

RUN mkdir -p -m 777 /var/log/mysql && touch /var/log/mysql/mysqld.log && chmod 777 /var/log/mysql/mysqld.log

RUN mkdir /root/scraper

ADD ./conf.d /etc/mysql/conf.d
ADD ./jobs.d /root/mysql/jobs.d

RUN chmod 777 /var/lib/mysql
RUN mkdir -p -m 777 /root/mysql/backup
