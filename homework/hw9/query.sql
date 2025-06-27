select * from borrowed;
select m.last_name, m.first_name, m.member_id, b.title, br.lib_name from borrowed br join book b on br.isbn = b.isbn join member m on br.member_id = m.member_id where br.checkin_date is null order by m.last_name, m.first_name;
