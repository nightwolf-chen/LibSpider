
create table books (
bookname varchar(255) not null,
author varchar(255) not null,
publisher varchar(255),
topic varchar(255),
categorycode varchar(45),
acquirecode varchar(45),
lang varchar(45),
borrow_count int default 0
);