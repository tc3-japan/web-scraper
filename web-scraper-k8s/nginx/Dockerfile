FROM  nginx:latest

RUN apt-get update && apt-get install -y \
    binutils \
    iputils-ping

RUN apt update && apt install -y \
    procps \
    iproute2 \
    vim

ADD ./libs /usr/share/nginx/html
