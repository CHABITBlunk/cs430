\W

select e.first_name, e.last_name, s.salary from employees e join salaries s on e.emp_no = s.emp_no where s.to_date = '9999-01-01' and s.salary > 90000;
select e.first_name, e.last_name, d.dept_name from employees e join current_dept_emp c on e.emp_no = c.emp_no join departments d on d.dept_no = c.dept_no where c.to_date = '9999-01-01' and d.dept_no = 'd008' or d.dept_no = 'd009';
select e.first_name, e.last_name, t.title from employees e join titles t on e.emp_no = t.emp_no where e.gender = 'F' and t.title = 'Technique Leader';
select e.first_name, e.last_name, s.salary from employees e join titles t on e.emp_no = t.emp_no join salaries s on e.emp_no = s.emp_no where t.title != 'Senior Engineer' and s.to_date = '9999-01-01' order by s.salary asc;
select first_name, last_name, birth_date from employees order by birth_date desc;
select e.first_name, e.last_name from employees e join dept_manager dm on e.emp_no = dm.emp_no where dm.to_date = '9999-01-01';
select e.first_name, e.last_name, d.dept_name from employees e join dept_emp de on e.emp_no = de.emp_no join departments d on d.dept_no = de.dept_no join salaries s on s.emp_no = e.emp_no where s.salary = (select max(salary) from salaries where to_date = '9999-01-01') and s.to_date = '9999-01-01';
