# Web Scraper Kubernetes

## Requirements

 - Kubernetes Cluster Creation Tool 
	 - Such as Kubeadm, Kubespray, Kops and so on.
 - kubectl

## Prerequisites

Create Kubernetes cluster in advance by a Kubernetes cluster creation tool.

## Configurations

Following can be configured in Kubernetes objects yaml for your environment.

 1. Increase `replicas` if needed
	 in scraper-app-deployment.yaml and scraper-web-deployment.yaml

 2. Change `volumeMounts` and `volumes` for each deployment yaml

## Create / Update Resources

`kubectl apply -k manifest/`

## Scraping

### Change Detection Init Job

`kubectl apply -f manifest/scraper-change-detection-init-job.yaml`

### Purchase History Job

`kubectl apply -f manifest/scraper-purchase-history-job.yaml`

### Product Job

`kubectl apply -f manifest/scraper-product-job.yaml`

### Group Products Job

`kubectl apply -f manifest/scraper-group-products-job.yaml`

### Run Scraping Command in scraper-app Pod

`kubectl exec -it deployment.apps/scraper-app -- bash -c "cd /root/scraper/; export SCRAPER_DB_PORT_3306_TCP_ADDR=scraper-db; java -jar web-scraper-server-*.jar  --spring.config.location=file:application.yaml --batch=purchase_history --site={EC Name}"`

Please see [scraper-app description](../web-scraper-server/README.md) for more details

## Delete Resources

`kubectl delete -k manifest/`

## Consideration

If large scale scraper application is built for many users, following are needed to consider.

 1. DNS for scraper-app and scraper-web pods
 2. Secrets Object for MySQL password in scraper-db-deployment

