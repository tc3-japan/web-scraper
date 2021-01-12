# web-scraper-front

## Requirements

- node 8.11.x
- vue.js

## config

you can update baseApi in *./src/config.ts*

## Install

```
npm install
npm run lint
```

## Build Admin App

```
npm run build
```

## Run Admin App

```
npm run serve
```

after success run, you can open http://127.0.0.1:8080/

click "Click here to EC site settings" to EC site settings page

## Build User App

```
npm run build-user
```

## Run Admin App

```
npm run serve-user
```

after success run, you can open http://127.0.0.1:8081/[id]/[siteId]

- id: User ID in user table hashed by BCrypt
- siteId: Site ID in ec_site_account table
