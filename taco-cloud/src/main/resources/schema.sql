CREATE SEQUENCE IF NOT EXISTS hibernate_sequence 
  START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS user (
  id IDENTITY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(60) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  street VARCHAR(50) NOT NULL,
  city VARCHAR(50) NOT NULL,
  state VARCHAR(2) NOT NULL,
  zip VARCHAR(10) NOT NULL,
  phone_number VARCHAR(10) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS taco_order (
  id IDENTITY,
  user_id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  street VARCHAR(50) NOT NULL,
  city VARCHAR(50) NOT NULL,
  state VARCHAR(2) NOT NULL,
  zip VARCHAR(10) NOT NULL,
  cc_number VARCHAR(16) NOT NULL,
  cc_expiration VARCHAR(5) NOT NULL,
  cc_cvv VARCHAR(3) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

ALTER TABLE taco_order ADD FOREIGN KEY (user_id) REFERENCES user(id);


CREATE TABLE IF NOT EXISTS taco (
  id IDENTITY,
  name VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS taco_order_tacos (
  order_id bigint NOT NULL,
  tacos_id bigint NOT NULL
);

ALTER TABLE taco_order_tacos ADD FOREIGN KEY (order_id) REFERENCES taco_order(id);

ALTER TABLE taco_order_tacos ADD FOREIGN KEY (tacos_id) REFERENCES taco(id);
    

CREATE TABLE IF NOT EXISTS ingredient (
  id VARCHAR(4) NOT NULL,
  name VARCHAR(25) NOT NULL,
  type VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS taco_ingredients (
  taco_id bigint NOT NULL,
  ingredients_id VARCHAR(4) NOT NULL
);

ALTER TABLE taco_ingredients
  ADD FOREIGN KEY (taco_id) REFERENCES taco(id);

ALTER TABLE taco_ingredients
  ADD FOREIGN KEY (ingredients_id) REFERENCES ingredient(id);