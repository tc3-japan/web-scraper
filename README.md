# Website Scraper

## Requirements

- Java 8
- Gradle 3.5
- Spring Boot 1.5.7

## Configuration

### configuration file

The app configuration can be changed in `./src/main/resources/application.yaml`

The following variables need to be configured correctly:

- `amazon.username` the username for amazon
- `amazon.password` the password for amazon

### environment variables

Values could be configured by environment variables also:

- `AMAZON_USERNAME`
- `AMAZON_PASSWORD`

### arguments

Values could be configured by providing arguments:

For example

`java -Damazon.username=helloworld -jar build/libs/web-scraper-0.0.1.jar`

### property file

`java -jar build/libs/web-scraper-0.0.1.jar --spring.config.location=file:{somewhere}/application-external.yml`

There are several ways to update configuration, for detail, please check spring boot official docs:
https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html


## Local run from source code

`./gradlew bootRun`

To specify site, specify site argument

`./gradlew  bootRun -Drun.arguments=--site=amazon`

If no site is specified, all sites will be run (currently only amazon is implemented)

## Local run from jar

- `./gradlew build`
- `java -jar build/libs/web-scraper-0.0.1.jar`

To specify site, specify site argument

- `java -jar build/libs/web-scraper-0.0.1.jar --site=amazon`

If no site is specified, all sites will be run (currently only amazon is implemented)


## Verification

- change configure, updating amazon username and password either in configuration file, or through environment
	variables, or through other way
- run though gradle, or jar file
- check `./amazon` folder, `login-*.html` are initial pages after login and `history-*.json` are purchase histories.
- to verify incremental save, edit history json file, remove one order. Then rerun application, there should be a new json file containing removed order. 
