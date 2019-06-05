CREATE TABLE user (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  email_for_contact VARCHAR (255),
  total_ec_status VARCHAR (255),
  id_expire_at TIMESTAMP,
  update_at TIMESTAMP
);

CREATE TABLE ec_site_account (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  ec_site VARCHAR (255),
  ec_use_flag tinyint (1),
  user_id INT,
  login_id_email VARCHAR (255),
  password VARCHAR (255),
  auth_cookies MEDIUMTEXT,
  auth_status VARCHAR(255),
  auth_fail_reason VARCHAR (1023),
  update_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id)
);
