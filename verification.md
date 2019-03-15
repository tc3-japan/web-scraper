## Steps

- first follow readme run rest api server(must import test data) and frontend

- after you successful run rest-api server and frontend, use browser open `http://localhost:8080/#/users/$2a$10$F7KztiRnl.GJL9I8l4.WUeEdQDiPosNmg8vUqV.rYdihzLEVb.4Me/ec-site-settings`
  - you can try to update params to check user not exist error
  - you can update `id_expire_at` in user table, to check user id exipred error
- click "Try to login " button to login page
  - the first CAPTCHA (before login page) usually does not appear
  - enter user name and password click login, then the CAPTCHA will appear (you may need try many times), after enter CAPTCHA code, then click login
  - it maybe need you enter Verification code again like CAPTCHA image, or return "ec site settings" page directly if successful
  - then you can see "**Login success** in "ec-site-settings" page
- after login success in vue.js web app, you can run `./gradlew clean build -x test && java -jar build/libs/web-scraper-develop-0.0.1.jar --batch=purchase_history ` in *web-scraper-develop* folder,  it will save htmls(**purchase-history*.html** in ./web-scraper-develop/logs/amazon folder) and save data into database (**purchase_history** table)



## Note

- from previous code, crawler did not support wrong email and password,  please enter your right email and password to test
- in the pptx page 9,  we must need fetch purchase history from all EC Site Accounts in DB,  it is mutually exclusive with the previous implement, so we need remove previous (set username and passwor in ENV)