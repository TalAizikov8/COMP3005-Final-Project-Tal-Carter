--making sure nothing is in any of the tables 
delete from book_into_basket;
delete from c_order;
delete from book;
delete from report;
delete from contacts;
delete from owner;
delete from publisher;
delete from customer;
delete from bank_account;

--bank account generate id by themselfs and the default money is 10000
insert into bank_account Default Values;
insert into bank_account Default Values;
insert into bank_account Default Values;
insert into bank_account Default Values;
insert into bank_account Default Values;
insert into bank_account Default Values;
insert into bank_account Default Values;
insert into bank_account Default Values;
--making a customer and assigning the bank account to them
insert into customer values (default, 10000000, 'Joey Dooda', '123 Main Street' , '123456789');
insert into customer values (default, 10000001, 'Lissy Moody', '22  Hoews Circle' , '1234588888');
--making a publisher and assigning the bank account to them
insert into publisher values (default, 10000002, 'Bossy Worldy', '1 Wall Street', 'Bossy@Wall.com', '1111111111');
insert into publisher values (default, 10000003, 'Law Order', '1 Court Main', 'Law@order.com', '1212121212');
insert into publisher values (default, 10000004, 'Smith Smith', '33 Holy Smith Street', 'smith@smith.com  ', '1212121212');
--making an owner and assigning the bank account to them
insert into owner values (default, 10000005, 'Many Shop', '321 Store Ave', '3344334433');
insert into owner values (default, 10000006, 'Rip Off', '321 Steal Street', '1569518222');
insert into owner values (default, 10000007, 'Mafia Boss', '321 Gank Ave', '2224442425');
--making many books each book has a publisher and an owner
insert into book values (default, 30000000, 40000000, 'The Most Interesting Book', 'Action','Inter Esting', 469, 15.99, 20, 0.23);
insert into book values (default, 30000000, 40000000, 'The Least Interesting Book', 'Action','Inter Esting', 694, 20.99, 25, 0.15);
insert into book values (default, 30000000, 40000000, 'A Moderately Interesting Book', 'Action', 'Inter Esting', 542, 17.99, 20, 0.31);
insert into book values (default, 30000001, 40000000, 'How to Eat A Lot','Cooking', 'Fat Joe', 333, 55.99, 25, 0.17);
insert into book values (default, 30000001, 40000000, 'Everything About Candles', 'Lifestyle', 'Free Ki', 15, 5.99, 30, 0.2);
insert into book values (default, 30000000, 40000001, 'How to Stay Up', 'Lifestyle','Free Ki', 100, 19.99, 30, 0.3);
insert into book values (default, 30000000, 40000001, 'The Universe and Beyond', 'Science', 'Buzz Lightyear', 980, 33.99, 25, 0.06);
insert into book values (default, 30000001, 40000001, 'Databases for Dummies', 'Computer-Science','Day Tah',  777, 99.99, 50, 0.5);
--This is a table which shows which owner and publisher are in contact
----If an owner has a book from a publisher they will be in contact, it also does that in the java
insert into contacts values (40000000, 30000000);
insert into contacts values (40000001, 30000000);
insert into contacts values (40000000, 30000001);
insert into contacts values (40000001, 30000001);

insert into report values ('19122021', 40000000, 0,0) --this is the first day the store has been opened. 

--Note some tables have not been inserted to. Specificaly report, c_order, book_into_basket
----This is because no orders have been made. Orders should be made through the java (aka actual book store)
