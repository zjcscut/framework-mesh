CREATE DATABASE test;

USE test;

CREATE TABLE t_customer
(
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(32)     NOT NULL
);