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
% tree docker
docker
├── README.md                          <--- This file
├── docker-compose.ecs.yml             <--- to run container at Amazon ECS
├── docker-compose.local.yml           <--- to build docker image and run container locally
├── ecs-params.yml                     <--- docker-compose support file at Amazon ECS
└── scraper
    └── build
        ├── Dockerfile                 <--- to build the application's docker image
        └── libs
            ├── application.yaml       <--- Application config file for deploying docker container
            └── web-scraper-0.0.1.jar  <--- Application Jar built with $PROJECT_HOME/gradlew build
```

### Step 1: Creating ECR Repository
```bash
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
```
__xxxxxxxxxxxx__ here is AWS_ACCOUNT_ID. Please remember that.
871766548585
871766548585.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-app

### Step 2: Build the Docker Image
```bash
% cd $PROJECT_HOME/docker
% docker-compose -f docker-compose.local.yml build
```

### Step 3: Add tag to Docker Image
```bash
% docker tag scraper:0.0.1 xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-app
% docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-app   latest              2a9bdcd0d9ce        2 minutes ago       280MB
scraper             0.0.1               2a9bdcd0d9ce        4 seconds ago       280MB
openjdk             8-jdk-slim          f1313c1cebfd        5 weeks ago         244MB
```

### Step 4: Push Docker Image to ECR Repository

#### Change terminal session into Docker-Login-State to Amazon ECR
```bash
% aws ecr get-login --no-include-email
docker login -u AWS -p eyJwYX...(SNIP)...MjAxfQ== https://xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com
% (paste above here and run it)

```
#### Push Docker Image 
```bash
% docker push xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/scraper-app
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
% ecs-cli up --keypair scraper-dev --capability-iam --instance-type t2.medium --cluster-config scraper-cfg
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
% aws ec2 authorize-security-group-ingress --group-id sg-xxxxxxxxxxxxxxxxx --protocol tcp --port 22 --cidr 0.0.0.0/0
```

### Step 4: Deploy the Compose File to a Cluster 

#### Change directory where docker files exists
```bash
% cd $PROJECT_HOME/docker
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

#### Connect EC2 Instance(Docker Engine)
```bash
% ssh -i "scraper-dev.pem" ec2-user@xxx.xxx.xxx.xxx
```

#### Connect Scraper Container
```bash
% docker ps
% docker exec -ti xxxxxxxxxxxx bash
```

#### Set AMAZON-Retated Environment Variables
```bash
% export AMAZON_USERNAME=xxxxxxxxxxxx
% export AMAZON_PASSWORD=xxxxxxxxxxxx
```

#### Execute Scraper 
```bash
% cd /root/scraper/
% java -jar web-scraper-0.0.1.jar --spring.config.location=file:application.yaml
```