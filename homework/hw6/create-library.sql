drop view if exists borrowed_by;
drop table if exists borrowed;
drop table if exists member, book, publisher, author, phone;

create table member (
  member_id   int           not null,
  first_name  varchar(10)   not null,
  last_name   varchar(10)   not null,
  dob         date          not null,
  primary key(member_id)
);

create table book (
  isbn            varchar(15) not null,
  title           varchar(35) not null,
  year_published  date        not null,
  primary key(isbn)
);

create table publisher (
  pub_id    int         not null,
  pub_name  varchar(25) not null,
  primary key(pub_id)
);

create table phone (
  p_number    int         not null,
  phone_type  varchar(20) not null,
  primary key(p_number)
);

create table author (
  author_id   int not null,
  first_name  varchar(10),
  last_name   varchar(10),
  primary key(author_id)
);

create table borrowed (
  member_id       int           not null,
  isbn            varchar(15)   not null,
  checkout_date   date          not null default current_date,
  checkin_date    date,
  primary key(member_id, isbn, checkout_date),
  foreign key(member_id) references member(member_id),
  foreign key(isbn) references book(isbn)
);

create or replace sql security invoker view borrowed_by as
  select isbn, member_id, checkin_date, checkout_date from borrowed;
