CREATE TABLE tactic_event (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  contents MEDIUMTEXT,
  status VARCHAR(31),
  create_at TIMESTAMP(3),
  finish_at TIMESTAMP(3)
);

CREATE TABLE request_event (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  contents MEDIUMTEXT,
  status VARCHAR(31),
  create_at TIMESTAMP(3),
  finish_at TIMESTAMP(3),
  tactic_event_id INT,
  FOREIGN KEY (tactic_event_id) REFERENCES tactic_event(id)
);
