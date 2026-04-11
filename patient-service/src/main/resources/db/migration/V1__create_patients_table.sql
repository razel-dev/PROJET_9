CREATE TABLE IF NOT EXISTS patients (
  id BIGINT PRIMARY KEY,
  first_name      VARCHAR(100) NOT NULL,
  last_name       VARCHAR(100) NOT NULL,
  date_of_birth   DATE         NOT NULL,
  gender          VARCHAR(16)  NOT NULL,
  address         VARCHAR(255),
  phone_number    VARCHAR(32)
);