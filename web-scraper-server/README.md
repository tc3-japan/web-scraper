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


### arguments

Values could be configured by providing arguments:

For example

`java -Dspring.datasource.url= -jar build/libs/web-scraper-0.0.1.jar`

### property file

`java -jar build/libs/web-scraper-0.0.1.jar --spring.config.location=file:{somewhere}/application-external.yml`

There are several ways to update configuration, for detail, please check spring boot official docs:
https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html

### Monitor target definition and check items definition

Please check `resources/monitor-target-definition.yaml` and `resources/check-items-definition.yaml`.

This configutration is used in batch `change_detection_init` and `change_detection_check` that is described lator.

### Traffic control
The app traffic control file can be changed in `web-scraper-server/src/main/resources/tactic.yaml`

The `proxy_server` must be a vaild value, other values can be default value

- You also can load external tactic config file when run, just like this 

  `./gradlew bootRun -PjvmArgs=-DtacticFile=tactic.yaml -Pargs=--rest`
  `./gradlew bootRun -Pargs=--batch=product `

  `java -DtacticFile=tactic.yaml -jar ./build/libs/web-scraper-server-0.0.1.jar  --rest`
  `java -DtacticFile=tactic.yaml -jar ./build/libs/web-scraper-server-0.0.1.jar  --batch=product`

  note :  `-DtacticFile=tactic.yaml ` must be before `-jar`


## Local run from source code

### First, run mysql inside docker

`docker-compose up`

### In a new terminal, run

`./gradlew bootRun -Pargs=--batch=purchase_history` to fetch purchase histories

`./gradlew bootRun -Pargs=--batch=product ` to fetch products which is stored when purchase history has been scraped

`./gradlew bootRun -Pargs=--batch=change_detection_init` to fetch initial data of purchase history and product to detect site change.

`./gradlew bootRun -Pargs=--batch=change_detection_check` to fetch current data of history and product and check them, comparing them to initital data.

`./gradlew bootRun -Pargs=--batch=group_products` to group each EC sites' products in product table.

`./gradlew bootRun -Pargs=--batch=load_product_index` to create Solr index for the feature, grouping products by name, in batch `group_product`. You usually run it before running batch `group_product`.

`./gradlew bootRun -Pargs=--rest ` to run rest api server


#### To specify site, specify `site` argument

`./gradlew bootRun -Pargs=--batch=purchase_history,--site=amazon`

If no `site` is specified, all sites will be run

#### To specify which module type will be selected in running, spedify `module` argument  

`./gradlew bootRun -Pargs=--batch=purchase_history,--module=unified`

If no `module` is specified, `unified` module is selected in running.
This argument is used only when some batch runs such as `purchase_history`, `product`, `change_detection_init`, and `change_detection_check`.
 

##### Module Types:

Module type is implementation architecture of scraping modules.

* unified(default): Each EC sites' scraping modules are unified in single series of classes named such as `GeneralProductModule` or so.
* isolated: Each EC sites' scraping modules are separated in multiple series of classes named such as `<EC-Name>ProductModule` or so. And common features of them are gathered in such as `AbstarctProductModule`.

#### API for scraper supporting features

This project includes API that provides supporting features for scraping and works together with Screeen features.
(see also web-scraper/web-scraper-front/README.md)

You can do below features using scraper supporting features like follows.
 
* Managing Login for each EC sites. 

* Managing product grouping by showing products grouped already, grouping products and ungrouping products.


## Local run from jar

- `./gradlew clean build -x test` to build jar file
- `./gradlew clean build -x test -Psecret` to build jar file with secret used when encrypting confidential information in database

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

./gradlew bootRun -Pargs=--batch=encrypt_user,--url_base=http://127.0.0.1:8085,--output_file=output.txt,--get_failed_logins
java -jar build/libs/web-scraper-server-0.0.1.jar --batch=encrypt_user --url_base=http://127.0.0.1:8085 --output_file=output.txt --get_failed_logins
