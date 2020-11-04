FROM centos:8

RUN yum update -y && yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel

RUN mkdir /root/scraper

COPY ./libs/web-scraper-server*.jar /root/scraper/
COPY ./libs/application.yaml  /root/scraper/application.yaml
