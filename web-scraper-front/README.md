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

after success run, you can open 

http://127.0.0.1:8081/[id]
or
http://127.0.0.1:8081/[id]/[siteId]

- id: User ID in user table hashed by BCrypt
- siteId: Site ID in ec_site_account table

currently following fixed 3 users can login for EC-site scraping.

1. http://localhost:8081/#/$2a$10$F7KztiRnl.GJL9I8l4.WUeEdQDiPosNmg8vUqV.rYdihzLEVb.4Me
2. http://localhost:8081/#/$2a$10$P6CimhjlJPSmk0B5pERDGODxh1xyPsixVKqplcLl.EFNE6eZ.vGoi
3. http://localhost:8081/#/$2a$10$ksERrFAMRwsEptWCe.VUg.9.G6WV6y5zt6XCF1GrUjDgoDgjKaG6a

## Copy for docker build

following command needs to be executed for docker build to copy apps of admin and user.

```
./scripts/copy-to-k8s.sh
```
