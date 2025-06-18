drop view if exists borrowed_by;
drop table if exists located_at;
drop table if exists borrowed;
drop table if exists book_author;
drop table if exists phone_owner;
drop table if exists member, book, author, phone, publisher;

create table library (
  lib_name varchar(20)  not null,
  street varchar(30)    not null,
  city varchar(30)      not null,
  state varchar(2)      not null,
  primary key(lib_name)
);

create table publisher (
  pub_id    int         not null,
  pub_name  varchar(25) not null,
  primary key(pub_id)
);

create table member (
  member_id   int         not null,
  first_name  varchar(10) not null,
  last_name   varchar(10) not null,
  gender      char        not null,
  dob         date        not null,
  primary key(member_id)
);

create table book (
  isbn            varchar(15) not null,
  title           varchar(35) not null,
  year_published  date        not null,
  pub_id          int         not null,
  primary key(isbn),
  foreign key(pub_id) references publisher(pub_id)
);

create table phone (
  p_number    varchar(12) not null,
  phone_type  varchar(10),
  primary key(p_number)
);

create table phone_owner (
  p_number varchar(12) not null,
  owner_type varchar(10) not null,
  owner_id int not null,
  primary key (p_number, owner_type, owner_id),
  foreign key (p_number) references phone(p_number)
);

create table author (
  author_id   int not null,
  first_name  varchar(10),
  last_name   varchar(10),
  primary key(author_id)
);

create table book_author (
  isbn  varchar(15) not null,
  author_id int     not null,
  primary key (isbn, author_id),
  foreign key (isbn) references book(isbn),
  foreign key (author_id) references author(author_id)
);

create table borrowed (
  member_id       int           not null,
  isbn            varchar(20)   not null,
  checkout_date   date          not null default current_date,
  checkin_date    date,
  primary key(member_id, isbn, checkout_date),
  foreign key(member_id) references member(member_id),
  foreign key(isbn) references book(isbn)
);

create table located_at (
  lib_name varchar(20)  not null,
  isbn varchar(15)      not null,
  total_copies     int  not null,
  copies_available int  not null check (copies_available >= 0),
  shelf_number int      not null,
  floor_number int      not null,
  primary key(lib_name, isbn),
  foreign key(lib_name) references library(lib_name),
  foreign key(isbn) references book(isbn)
);
