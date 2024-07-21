use Bank;


select * from accounts;


select * from customers;

select * from bankers;

select * from users;



INSERT INTO users (id, user_name, password, role) 
VALUES (1, 'Admin', 'kydew1-setbot-myJxux', 'ROLE_ADMIN');


commit;




drop table visa_cards;

drop table loans;

drop table currency_exchange_rates;

drop table transactions;

drop table accounts;

drop table bankers;

drop table customers;

drop table users;









UPDATE customers SET email ="abu@gmail.com" WHERE customer_id = 1;

select * from customers where customer_id = 1;





