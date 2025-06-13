select * from book order by isbn;
select * from member order by last_name, first_name;
select * from author order by last_name, first_name;
select * from publisher order by pub_name;
select * from phone order by p_number;

select * from phone_owner;
select * from borrowed;
select * from book_author;

select first_name, last_name from member where last_name like 'B%';

select b.isbn, b.title, b.year_published from book b
join publisher p on b.pub_id = p.pub_id
where p.pub_name = 'Coyote Publishing' order by b.title;

select 
  m.first_name,
  m.last_name,
  m.member_id,
  bk.isbn,
  bk.title,
  b.checkout_date
from member m
join borrowed b on b.member_id = m.member_id
join book bk on b.isbn = bk.isbn
where b.checkin_date is null
order by m.member_id, b.checkout_date;

select
  a.first_name,
  a.last_name,
  a.author_id,
  b.title
from author a
join book_author ba on a.author_id = ba.author_id
join book b on ba.isbn = b.isbn;

select
  a.first_name,
  a.last_name,
  p.p_number
from author a
join phone_owner po on a.author_id = po.owner_id
join phone p on po.p_number = p.p_number
where po.owner_type = 'author'
  and p.p_number in (
    select p_number
    from phone_owner
    where owner_type = 'author'
    group by p_number
    having count(distinct owner_id) > 1
  )
order by p.p_number, a.last_name, a.first_name;
