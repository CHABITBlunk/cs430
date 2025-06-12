load data local infile 'Publisher.csv' into table publisher fields terminated by ',' lines terminated by '\n' (pub_id, pub_name);

load data local infile 'Book.csv' into table book fields terminated by ',' lines terminated by '\n' (isbn, title, pub_id, @date) set year_published = str_to_date(@date, '%m/%d/%Y');

load data local infile 'Author.csv' into table author fields terminated by ',' lines terminated by '\n' (author_id, first_name, last_name);

drop table if exists raw_lines;

create table raw_lines (
  line_id int auto_increment primary key,
  raw_line text
);

load data local infile 'Members.csv' into table raw_lines lines terminated by '\n' (raw_line);

insert into member (member_id, first_name, last_name, dob)
  select cast(substring_index(raw_line, ',', 1) as unsigned) as member_id,
  substring_index(substring_index(raw_line, ',', 2), ',', -1) as first_name,
  substring_index(substring_index(raw_line, ',', 3), ',', -1) as last_name,
  str_to_date(substring_index(raw_line, ',', -1), '%m/%d/%Y') as dob
from raw_lines where length(raw_line) - length(replace(raw_line, ',', '')) = 4;

alter table raw_lines add column tag_member_id int;

update raw_lines
set tag_member_id = cast(substring_index(raw_line, ',', 1) as unsigned)
where raw_line rlike '^[0-9]+,';

set @last_member_id = null;

update raw_lines set tag_member_id = (
  case
    when raw_line not like ' %' then @last_member_id := tag_member_id
    else @last_member_id
  end
)
order by line_id;

select distinct tag_member_id
from raw_lines
where raw_line like ' %'
and tag_member_id not in (select member_id from member);

select count(*) from raw_lines
where raw_line like ' %' and tag_member_id is null;

insert into borrowed (member_id, isbn, checkout_date, checkin_date)
select
  tag_member_id, substring_index(raw_line, ',', 1) as isbn,
  str_to_date(substring_index(substring_index(raw_line, ',',  2), ',', -1), '%m/%d/%Y') as checkout_date,
  str_to_date(substring_index(raw_line, ',', -1) ,'%m/%d/%Y') as checkin_date
from raw_lines where raw_line like ' %';

select b.* from ( select tag_member_id as member_id, substring_index(raw_line, ',', 1) as isbn, str_to_date(substring_index(substring_index(raw_line, ',', 2), ',', -1), '%m/%d/%Y') as checkout_date, str_to_date(substring_index(raw_line, ',', -1), '%m/%d/%Y') as checkin_date from raw_lines where raw_line like ' %') b left join member m on b.member_id = m.member_id where m.member_id is null;
