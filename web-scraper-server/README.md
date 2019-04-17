# Website Scraper

## Requirements

- Java 8
- Gradle 3.5
- Spring Boot 1.5.7
- Docker
- Mysql 8+ (if you did not use docker mysql)

## Configuration

### configuration file

The app configuration can be changed in `./src/main/resources/application.yaml`

The following variables need to be configured correctly:

- `spring.datasource.url` mysql url
- `spring.datasource.username` mysql username
- `spring.datasource.password` mysql password


the app traffic contro file can be changed in `web-scraper-server/src/main/resources/tactic.yaml`

the `proxy_server` must be a vaild value, other values can be default value

- you also can load external tactic config file when run, just like this 

  `java -DtacticsFile=tactic.yaml -jar ./build/libs/web-scraper-server-0.0.1.jar  --rest`

  note :  `-DtacticsFile=tactic.yaml ` must be before `-jar`

### arguments

Values could be configured by providing arguments:

For example

`java -Dspring.datasource.url= -jar build/libs/web-scraper-0.0.1.jar`

### property file

`java -jar build/libs/web-scraper-0.0.1.jar --spring.config.location=file:{somewhere}/application-external.yml`

There are several ways to update configuration, for detail, please check spring boot official docs:
https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html

## Monitor target definition and check items definition

Please check `resources/monitor-target-definition.yaml`
and `resources/check-items-definition.yaml`

It is required to have environment variable `AMAZON_CHECK_TARGET_KEYS_PASSWORDS`,
which contains comma separated passwords, which match `check_target_keys` for `purchase_history_list` page.
The order matters!

## Local run from source code

First, run mysql inside docker

`docker-compose up`

In a new terminal, run

`./gradlew bootRun -Pargs=--batch=purchase_history` to fetch purchase histories

`./gradlew bootRun -Pargs=--batch=product ` to fetch products (purchased products)

`./gradlew bootRun -Pargs=--rest ` to run rest api server


To specify site, specify site argument

`./gradlew bootRun -Pargs=--batch=purchase_history,--site=amazon`

If no site is specified, all sites will be run (currently only amazon is implemented)

## Local run from jar

- `./gradlew clean build -x test` to build jar file
- `java -jar build/libs/web-scraper-server-0.0.1.jar --batch=purchase_history`

To specify site, specify site argument

- `java -jar build/libs/web-scraper-server-0.0.1.jar --batch=purchase_history --site=amazon`, If no site is specified, all sites will be run (currently only amazon is implemented)
- or use `java -jar build/libs/web-scraper-server-0.0.1.jar --rest` to run rest api server, default port is 8085

## Import test data

after success run rest api server, the databse table will be auto created,  now

import *./docs/test-data.sql* into mysql database (web_scraper database)

## Scraper Verification

### File based purchase history 

- change configure, updating amazon username and password either in configuration file, or through environment
  variables, or through other way
- run though gradle, or jar file
- check `./logs/amazon` folder, `login-*.html` are initial pages after login and `history-*.json` are purchase histories.
- to verify incremental save, edit history json file, remove one order. Then rerun application, there should be a new json file containing removed order. 

### Mysql purchase history

- change configure, updating mysql connect information, updating amazon username and password
    changes could be made either in configuration file, or through environment
	variables, or through other way
- run though gradle, or jar file
- check `./logs/amazon` folder, `login-*.html` are initial pages after login and `purchase-history-*.json` are purchase histories pages.
- to verify incremental save, delete last record in mysql. Then rerun application, there should be one new row containing removed order. 

To connect to mysql in docker, please specify host as `0.0.0.0`, see below:

> `mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper`


### Create CSV of encrypted user ids / email addresses:
Run the script using the --batch=encrypt_user option.
- set url base parts (scheme, host, and port) with --url_base=\<scheme://host:port>, If no url_base is specified, "http://127.0.0.1:8085" is used.
- set output file with --output_file=\<file>. If no output_file is specified, a file called encryptedUsers.txt will be created.
- set flag to limit users who have login failure ec site with get_failed_logins, If no get_failed_logins is specified, all users will be processed.

java -jar build/libs/web-scraper-server-0.0.1.jar --batch=encrypt_user --url_base=http://127.0.0.1:8085 --output_file=output.txt --get_failed_logins
