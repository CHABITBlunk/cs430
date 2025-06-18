insert into book (isbn, title, year_published, pub_id)
values ('96-42013-10510', 'Growing your own Weeds', '2012-06-24', 10000);
insert into located_at (lib_name, isbn, shelf_number, floor_number)
values ('Main', '96-42013-10510', 8, 2);

update located_at
set total_copies = 8
where isbn = '96-42103-10907' and lib_name = 'Main';

delete from author
where first_name = 'Grace' and last_name = 'Slick';

insert into author (author_id, first_name, last_name)
values (305, 'Commander', 'Adams');
insert into phone values('970-555-5555', 'office');
insert into phone_owner values('970-555-5555', 'author', 305)

insert into book (isbn, title, year_published, pub_id)
values ('96-42013-10510', 'Growing your own Weeds', '2012-06-24', 10000);
insert into located_at (lib_name, isbn, shelf_number, floor_number)
values ('South Park', '96-42013-10510', 8, 3);

delete from located_at la
join book b on la.isbn = b.isbn
where la.lib_name = 'Main' and b.title = 'Missing Tomorrow';

update located_at la 
set total_copies = total_copies + 2
join book b on la.isbn = b.isbn
where lib_name = 'South Park' and b.title = 'Eating in the Fort';

insert into book (isbn, title, year_published, pub_id)
values ('96-42013-10513', 'Growing your own Weeds', '2012-06-24', 90000);
insert into located_at (lib_name, isbn, shelf_number, floor_number)
values ('Main', '96-42013-10513', 8, 2);

select * from audit_log;
