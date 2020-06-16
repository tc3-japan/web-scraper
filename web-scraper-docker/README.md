# AWS Environment for Website Scraper


--------------------------------------------------------------------------------
## Prerequisites

It is expected that you have completed the following prerequistes before continuing on:

* Set up an AWS account.
* Install the ECS CLI. For more information, see: [Installing the Amazon ECS CLI](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_CLI_installation.html).
* Install and configure the AWS CLI. For more information, see [AWS Command Line Interface](https://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html).


--------------------------------------------------------------------------------
## Configure AWS Access

### Step 1: Create AWS Profile to local work terminal
```bash
% aws configure --profile scraper-dev
AWS Access Key ID [None]: ********
AWS Secret Access Key [None]: ********
Default region name [None]: ap-northeast-1
Default output format [None]: json

% aws configure --profile scraper-poc
AWS Access Key ID [None]: ********
AWS Secret Access Key [None]: ********
Default region name [None]: ap-northeast-1
Default output format [None]: json
```

### Step 2: Set AWS Profile to terminal session
```bash
% export AWS_PROFILE=scraper-dev
```
In this README, we assume to be set to scraper-dev
 
--------------------------------------------------------------------------------
## Setup ECR and Push Docker Image to ECR Repository

### Step 0: Confirm AWS/Docker-Related files
```bash
% tree web-scraper-docker
web-scraper-docker
├── README.md                                 <--- This file
├── docker-compose.ecs.yml                    <--- to run container at Amazon ECS
├── docker-compose.local.yml                  <--- to build docker image and run container locally
├── ecs-params.yml                            <--- docker-compose support file at Amazon ECS
├── mysql
│   └── build
│       ├── Dockerfile                        <--- to build the mysql docker image
│       ├── conf.d                            <--- mysql additional config files
│       │   └── web-scraper.cnf
│       └── initdb.d                          <--- mysql initialization SQLs
│           ├── encrypt-table.sql
│           └── test-data.sql
├── nginx
│   └── build
│       ├── Dockerfile                        <--- to build the nginx docker image
│       └── libs                              <--- to build the front apps docker image
│           ├── app.a6f649c2.css
│           ├── css
│           │   └── app.a6f649c2.css
│           ├── favicon.ico
│           ├── index.html
│           └── js
│               ├── app.d17e1534.js
│               ├── app.d17e1534.js.map
│               ├── chunk-vendors.c0172bdd.js
│               └── chunk-vendors.c0172bdd.js.map
├── scraper
│   └── build
│       ├── Dockerfile
│       └── libs
│           ├── application.yaml              <--- Application config file for deploying docker container
│           └── web-scraper-server-0.0.1.jar  <--- Application Jar built with $PROJECT_HOME/gradlew build
└── scripts
    ├── scp-to-aws.sh                         <--- Script to copy local files to AWS ECS host
    └── setup-local.sh
```

### Step 1: Creating ECR Repository
```bash
% aws ecr create-repository --repository-name scraper-web
{
    "repository": {
        "repositoryArn": "arn:aws:ecr:ap-northeast-1: xxxxxxxxxxxx:repository/scraper-web",
        "registryId": "xxxxxxxxxxxx",
        "repositoryName": "scraper-web",
        "repositoryUri": "xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-web",
        "createdAt": 1555169250.0
    }
}

% aws ecr create-repository --repository-name scraper-app
{
    "repository": {
        "repositoryArn": "arn:aws:ecr:ap-northeast-1:xxxxxxxxxxxx:repository/scraper-app",
        "registryId": "xxxxxxxxxxxx",
        "repositoryName": "scraper-app",
        "repositoryUri": "xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-app",
        "createdAt": 1539200225.0
    }
}

% aws ecr create-repository --repository-name scraper-db
{
    "repository": {
        "repositoryArn": "arn:aws:ecr:ap-northeast-1: xxxxxxxxxxxx:repository/scraper-db",
        "registryId": "xxxxxxxxxxxx",
        "repositoryName": "scraper-db",
        "repositoryUri": "xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-db",
        "createdAt": 1555169254.0
    }
}

% aws ecr create-repository --repository-name scraper-solr
{
    "repository": {
        "repositoryArn": "arn:aws:ecr:ap-northeast-1:xxxxxxxxxxxx:repository/scraper-solr",
        "registryId": "xxxxxxxxxxxx",
        "repositoryName": "scraper-solr",
        "repositoryUri": "xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-solr",
        "createdAt": "2020-05-29T11:59:55+09:00",
        "imageTagMutability": "MUTABLE",
        "imageScanningConfiguration": {
            "scanOnPush": false
        }
    }
}

```
__xxxxxxxxxxxx__ here is AWS\_ACCOUNT_ID. Please remember that.

xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-app

### Step 2: Build the Docker Image
```bash
% cd $PROJECT_HOME/docker
% docker-compose -f docker-compose.local.yml build
```

### Step 3: Add tag to Docker Image
```bash
% docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
scraper-web         latest              e3bd63f25585        9 days ago          176MB
scraper-app         latest              012c1c90109f        9 days ago          357MB
scraper-db          latest              e903ce5b62b0        9 days ago          543MB
scraper-solr        latest              112d64950dfb        9 days ago          493MB

% docker tag scraper-web:latest xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-web
% docker tag scraper-app:latest xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-app
% docker tag scraper-db:latest  xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-db
% docker tag scraper-solr:latest  xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-solr

% docker images
REPOSITORY                                                      TAG                 IMAGE ID            CREATED             SIZE
xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-web   latest              e3bd63f25585        9 days ago          176MB
scraper-web                                                     latest              e3bd63f25585        9 days ago          176MB
xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-app   latest              012c1c90109f        9 days ago          357MB
scraper-app                                                     latest              012c1c90109f        9 days ago          357MB
xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-db    latest              e903ce5b62b0        9 days ago          543MB
scraper-db                                                      latest              e903ce5b62b0        9 days ago          543MB
xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-solr  latest              112d64950dfb        9 days ago          493MB
scraper-solr                                                    latest              112d64950dfb        9 days ago          493MB
```

### Step 4: Push Docker Image to ECR Repository

#### Change terminal session into Docker-Login-State to Amazon ECR
```bash
% aws ecr get-login-password | docker login --username AWS --password-stdin https://xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com

```
#### Push Docker Image 
```bash
% docker push xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-web
% docker push xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-app
% docker push xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-db
% docker push xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-solr
```


--------------------------------------------------------------------------------
## Create Cluster and Start Docker Containers

### Step 1: Configure the ECS CLI 

#### To create an ECS CLI configuration:

##### 1. Create a cluster configuration:
```bash
% ecs-cli configure --cluster scraper-cluster --region ap-northeast-1 --default-launch-type EC2 --config-name scraper-cfg

% cat ~/.ecs/config
  version: v1
  default: scraper-cfg
  clusters:
    scraper-cfg:
      cluster: scraper-cluster
      region: ap-northeast-1
      default_launch_type: EC2
```

##### 2. Create a profile using your access key and secret key:   
```bash
% ecs-cli configure profile --access-key AWS_ACCESS_KEY_ID --secret-key AWS_SECRET_ACCESS_KEY --profile-name scraper-dev

% cat ~/.ecs/credentials
  version: v1
  default: scraper-dev
  ecs_profiles:
    scraper-dev:
      aws_access_key_id: AWS_ACCESS_KEY_ID
      aws_secret_access_key: AWS_SECRET_ACCESS_KEY
```

##### 3. Set ECS Profile to terminal session
```bash
% export ECS_PROFILE=scraper-dev
```
In this README, we assume to be set to scraper-dev


### Step 2: Create the Cluster

#### Create cluster
```bash
% ecs-cli up --keypair scraper-dev --capability-iam --instance-type t2.large --cluster-config scraper-cfg
INFO[0000] Using recommended Amazon Linux AMI with ECS Agent 1.20.3 and Docker version 18.06.1-ce
INFO[0000] Created cluster                               cluster=scraper-cluster region=ap-northeast-1
INFO[0001] Waiting for your cluster resources to be created...
INFO[0001] Cloudformation stack status                   stackStatus=CREATE_IN_PROGRESS
VPC created: vpc-xxxxxxxxxxxxxxxxx
Security Group created: sg-xxxxxxxxxxxxxxxxx
Subnet created: subnet-xxxxxxxxxxxxxxxxx
Subnet created: subnet-xxxxxxxxxxxxxxxxx
Cluster creation succeeded.
```

#### Add SSH rule to AWS security group
```bash
% aws ec2 authorize-security-group-ingress --group-id sg-xxxxxxxxxxxxxxxxx --ip-permissions IpProtocol=tcp,FromPort=22,ToPort=22,IpRanges='[{CidrIp=0.0.0.0/0,Description="ssh to docker engine"}]'

% aws ec2 authorize-security-group-ingress --group-id sg-xxxxxxxxxxxxxxxxx --ip-permissions IpProtocol=tcp,FromPort=8080,ToPort=8080,IpRanges='[{CidrIp=0.0.0.0/0,Description="docker: scraper-web"}]'

% aws ec2 authorize-security-group-ingress --group-id sg-xxxxxxxxxxxxxxxxx --ip-permissions IpProtocol=tcp,FromPort=8085,ToPort=8085,IpRanges='[{CidrIp=0.0.0.0/0,Description="docker: scraper-app"}]'

% aws ec2 authorize-security-group-ingress --group-id sg-xxxxxxxxxxxxxxxxx --ip-permissions IpProtocol=tcp,FromPort=8983,ToPort=8983,IpRanges='[{CidrIp=0.0.0.0/0,Description="docker: scraper-solr"}]'
```

### Step 4: Deploy the Compose File to a Cluster 

#### Change directory where docker files exists
```bash
% cd $PROJECT_HOME/web-scraper-docker
```

#### Set AWS_ACCOUNT_ID environment variable
```bash
export AWS_ACCOUNT_ID=xxxxxxxxxxxx
```

#### Deploy the Containers
##### Deploy with creating AWS Log Groups (When initial setup)
```bash
% ecs-cli compose --project-name scraper-prj  --file docker-compose.ecs.yml up --create-log-groups --cluster-config scraper-cfg
```
see: AWS console > (services) CloudWatch > Logs
##### Deploy without creating AWS Log Groups (When setup again)
```bash
% ecs-cli compose --project-name scraper-prj  --file docker-compose.ecs.yml up --cluster-config scraper-cfg
``` 

### Step 5: Undeploy the Containers (If the containers don't need any more)
```bash
% ecs-cli compose --project-name scraper-prj --file docker-compose.ecs.yml service rm --cluster-config scraper-cfg
```

### Step 6: Delete the Cluster (If the containers don't need any more)
```bash
% ecs-cli down --force --cluster-config scraper-cfg
```

### Step 7: Execute Scraper Application

#### Copy local files related to scraper-web and scraper-db
```bash
% ./scripts/scp-to-aws.sh <path/to/scraper-dev.pem> <aws ecs host>
```

#### Connect EC2 Instance(Docker Engine)
```bash
% ssh -i <path/to/scraper-dev.pem> ec2-user@<aws ecs host>
```

#### Connect Scraper Container
```bash
% docker ps
CONTAINER ID        IMAGE                                                                  COMMAND                  CREATED             STATUS              PORTS                               NAMES
3d169b42c646        xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-web:latest   "nginx -g 'daemon of…"   2 hours ago         Up 2 hours          0.0.0.0:8080->80/tcp                ecs-scraper-prj-34-scraper-web-80b8acecdfeb9dc58a01
f55871c2bc57        xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-app:latest   "bash -c 'sleep 30; …"   2 hours ago         Up 2 hours          0.0.0.0:8085->8085/tcp              ecs-scraper-prj-34-scraper-app-8caf84dda4b79dbed601
bf9b07bfc6e8        xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-db:latest    "docker-entrypoint.s…"   2 hours ago         Up 2 hours          0.0.0.0:3306->3306/tcp, 33060/tcp   ecs-scraper-prj-34-scraper-db-dcca859ba7a9a7bbf901
% docker exec -ti xxxxxxxxxxxx bash
```

#### Register scraper users to get purchase history
```bash
% docker exec -ti <scraper-db's CONTAINER ID> bash
$ mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < /root/mysql/initdb.d/test-data.sql
```

#### Execute Scraper 
```bash
% cd /root/scraper/
% java -jar web-scraper-server-0.0.1.jar --spring.config.location=file:application.yaml --batch=purchase_history
```
