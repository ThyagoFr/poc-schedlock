CREATE TABLE IF NOT EXISTS shedlock(
   name VARCHAR(64) NOT NULL,
   lock_until TIMESTAMP(3) NOT NULL,
   locked_at TIMESTAMP(3) NOT NULL,
   locked_by  VARCHAR(255),
   CONSTRAINT name_pk PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS users (
  id SERIAL,
  name VARCHAR(250) NOT NULL,
  surname VARCHAR(250) NOT NULL,
  address VARCHAR(250) DEFAULT NULL,
  CONSTRAINT users_pk PRIMARY KEY(id)
);