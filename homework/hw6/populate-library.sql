load data local infile 'Publisher.csv' into table publisher fields terminated by ',' lines terminated by '\n' (pub_id, pub_name);

load data local infile 'Book.csv' into table book fields terminated by ',' lines terminated by '\n' (isbn, title, pub_id, @date) set year_published = str_to_date(@date, '%m/%d/%Y');

load data local infile 'Author.csv' into table author fields terminated by ',' lines terminated by '\n' (author_id, first_name, last_name);

create table members_raw_lines (
  line_id int auto_increment primary key,
  raw_line text
);

load data local infile 'Members.csv' into table members_raw_lines lines terminated by '\n' (raw_line);

insert into member (member_id, first_name, last_name, dob)
  select cast(substring_index(raw_line, ',', 1) as unsigned) as member_id,
  substring_index(substring_index(raw_line, ',', 2), ',', -1) as first_name,
  substring_index(substring_index(raw_line, ',', 3), ',', -1) as last_name,
  str_to_date(substring_index(raw_line, ',', -1), '%m/%d/%Y') as dob
from raw_lines where raw_line not like ' %';

alter table raw_lines add column tag_member_id int;

update raw_lines
set tag_member_id = cast(substring_index(raw_line, ',', 1) as unsigned)
where raw_line not like ' %';

set @last_member_id = null;

update raw_lines set tag_member_id = (
  case
    when raw_line not like ' %' then @last_member_id := tag_member_id
    else last_member_id
  end
)
order by line_id;

insert into borrowed (member_id, isbn, checkout_date, checkin_date)
select
  tag_member_id, substring_index(raw_line, ',', 1) as isbn,
  str_to_date(substring_index(substring_index(raw_line, ',',  2), ',', -1), '%m/%d/%Y') as checkout_date,
  str_to_date(substring_index(raw_line, ',', -1) ,'%m/%d/%Y') as checkin_date
from raw_lines where raw_line like ' %';

load data local infile 'Borrowed.csv' into table borrowed fields terminated by ',' lines terminated by '\n' (isbn, )
