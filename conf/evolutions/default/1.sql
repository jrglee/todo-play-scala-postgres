# --- !Ups

CREATE TABLE IF NOT EXISTS todo (
  id        SERIAL,
  title     TEXT    NOT NULL,
  completed BOOLEAN NOT NULL
);

# --- !Downs

DROP TABLE todo;