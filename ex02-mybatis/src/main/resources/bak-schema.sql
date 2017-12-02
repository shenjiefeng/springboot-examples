-- h2 database; 运行只需要jar包，轻量级的内嵌数据库;
-- CREATE TABLE city (
--     id INT PRIMARY KEY auto_increment,
--     name VARCHAR,
--     state VARCHAR,
--     country VARCHAR
-- );


-- oracle
CREATE TABLE city (
    id VARCHAR2(32) NOT NULL ,
    name VARCHAR2(64),
    state VARCHAR2(64),
    country VARCHAR2(64),
    PRIMARY KEY(ID)
);

--创建自增ID，名称为：表名_字段名_SEQ
-- CREATE SEQUENCE CITY_ID_SEQ MINVALUE 1 NOMAXVALUE INCREMENT BY 1 START WITH 1 NOCACHE;

