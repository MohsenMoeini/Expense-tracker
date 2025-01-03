insert into categories(id, name) values (1,'Food');
insert into categories(id, name) values (2,'Transport');
insert into categories(id, name) values (3,'Health');
insert into categories(id, name) values (4,'Utilities');

insert into expense_threshold(id, category_id, monthly_threshold_amount, monthly_threshold_currency, total_monthly_expenses_amount, total_monthly_expenses_currency, username)
values (1, 1, 20, 'IRR', 0, 'IRR', 'moeini');
insert into expense_threshold(id, category_id, monthly_threshold_amount, monthly_threshold_currency, total_monthly_expenses_amount, total_monthly_expenses_currency, username)
values (2, 2, 500, 'IRR', 0, 'IRR', 'moeini');
insert into expense_threshold(id, category_id, monthly_threshold_amount, monthly_threshold_currency, total_monthly_expenses_amount, total_monthly_expenses_currency, username)
values (3, 3, 600, 'IRR', 0, 'IRR', 'moeini');