select * from library order by lib_name;

select * from located_at order by isbn;

select b.title, l.total_copies, l.lib_name
from book b
join located_at l on b.isbn = l.isbn
where b.isbn in (
  select isbn from located_at
  group by isbn
  having count(distinct lib_name) >= 2
)
order by b.title, l.lib_name;

select l.lib_name, count(distinct la.isbn) as num_titles
from library l
left join located_at la on l.lib_name = la.lib_name
group by l.lib_name
order by l.lib_name;

create table audit_log (
  id int auto_increment primary key,
  action      text not null,
  action_date date not null default current_date,
  action_time time not null default current_time
);

create trigger log_author_insert
after insert on author
for each row
insert into audit_log (action)
values (concat('added author id ', new.author_id));

create trigger log_book_add_to_library
after insert on located_at
for each row
insert into audit_log (action)
values (concat('added book ', new.isbn, ' to library ', new.lib_name));

create trigger log_book_remove_from_library
after delete on located_at
for each row
insert into audit_log (action)
values (concat('removed book ', old.isbn, ' from library ', old.lib_name));

delimiter // 
create trigger log_copies_updated
after update on located_at
for each row
begin
  if old.total_copies <> new.total_copies then
    insert into audit_log (action)
    values (concat('updated total copies of book ', new.isbn, ' at ', new.lib_name, ' from ', old.total_copies, ' to ', new.total_copies));
  end if;

  if old.copies_available <> new.copies_available then
    insert into audit_log (action)
    values (concat('updated available copies of book ', new.isbn, ' at ', new.lib_name, ' from ', old.copies_available, ' to ', new.copies_available));
  end if;
end;
//

delimiter ;

create or replace sql security invoker view books_authors_libraries as
select
  b.title as book_title,
  l.lib_name as library_name,
  group_concat(concat(a.first_name, ' ', a.last_name) order by a.last_name separator ', ') as authors
from book b
join book_author ba on b.isbn = ba.isbn
join author a on ba.author_id = a.author_id
join located_at la on b.isbn = la.isbn
join library l on la.lib_name = l.lib_name
group by b.title, l.lib_name;

select bal.*, la.shelf_number from books_authors_libraries bal
join book b on bal.book_title = b.title
join located_at la on b.isbn = la.isbn
order by bal.book_title;
