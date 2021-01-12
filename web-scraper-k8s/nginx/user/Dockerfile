FROM centos:8

RUN yum update -y && yum install -y nginx

ADD ./libs /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
