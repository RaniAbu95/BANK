use Bank;


select * from accounts;


select * from customers;

select * from bankers;

select * from users;

select * from visa_cards;

select * from visa_installments;

select * from transactions;



INSERT INTO users (id, user_name, password, role) 
VALUES (2, 'basel', 'toto8923toto', 'ROLE_USER');


commit;




drop table visa_cards;

drop table loans;

drop table currency_exchange_rates;

drop table transactions;

drop table accounts;

drop table bankers;

drop table customers;

drop table users;

drop table visa_installments;







UPDATE customers SET email ="abu@gmail.com" WHERE customer_id = 1;

select * from customers where customer_id = 1;





