
create table dataset (
userid varchar(20),
username varchar(20),
major varchar(45),
college varchar(45),
bookname varchar(200),
author varchar(200),
topic varchar(255),
lang varchar(10),
category varchar(45),
primary key (userid,bookname,author)
);
