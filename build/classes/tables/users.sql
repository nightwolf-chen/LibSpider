
create table users(
userid varchar(45) primary key,
username varchar(45) not null,
college varchar(45) not null,
major varchar(45) not null
);

select * from books where

bookname in (
select bookname from user_book where user_book.userid in 
(select userid_b from user_user where userid_a='20101003712' order by similarity desc limit 10)
)

and 

author in (
select author from user_book where user_book.userid in 

(select userid_b from user_user where userid_a='20101003712' order by similarity desc limit 10)
)

order by borrow_count desc;

