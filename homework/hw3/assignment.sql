\W

show databases;
use information_schema;
use jmaster;
show tables;
source ./createemp.sql;
show tables;
describe dept_emp, dept_manager, titles, salaries, employees, departments;
source ./loademp.sql;
source ./queryemp.sql;
