
create table user_user(
userid_a varchar(45),
userid_b varchar(45),
similarity real default 0.0,

primary key(userid_a,userid_b)
);
