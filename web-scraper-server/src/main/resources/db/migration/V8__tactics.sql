CREATE TABLE tactic_event (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  contents MEDIUMTEXT,
  status VARCHAR(31),
  create_at TIMESTAMP,
  finish_at TIMESTAMP
);

CREATE TABLE request_event (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  contents MEDIUMTEXT,
  status VARCHAR(31),
  create_at TIMESTAMP,
  finish_at TIMESTAMP,
  tactic_event_id INT,
  FOREIGN KEY (tactic_event_id) REFERENCES tactic_event(id)
);
