CREATE TABLE fruits
(
    id       SERIAL NOT NULL CONSTRAINT FRUITS_PK PRIMARY KEY,
    name       VARCHAR (50),
    datetime TIMESTAMP
);

CREATE UNIQUE INDEX fruits_id_index
    ON fruits (id);