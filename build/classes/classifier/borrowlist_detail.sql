
create table dateset (
userid varchar(20),
username varchar(20),
major varchar(45),
college varchar(45),
bookname varchar(200),
author varchar(200),
topic varchar(255),
categorycode varchar(45),
lang varchar(10),
primary key (userid,bookname,author)
);
