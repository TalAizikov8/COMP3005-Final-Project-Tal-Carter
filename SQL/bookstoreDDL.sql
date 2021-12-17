--Creating a bank account
create table bank_account
	(b_id		integer GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 10000000), --starting at 10000000 and adding one for each next bank account to generate a new id
	 money	numeric(15, 2) not null DEFAULT 10000, --cant have null money and each person we assume starts with 10000
	 primary key (b_id) --the id is the primary key
	);
--Creating a customer
----Can buy book and view books. Can also create a new customer.
create table customer
	(c_id		integer GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 20000000), --Here we start at 20000000 overlap is allowed but to make it easier to follow added 10000000 to different IDs
	 b_id		integer,
	 name		varchar(20) not null,
	 address	varchar(30) not null,
	 phone		varchar(10),
	 primary key (c_id),
	 foreign key (b_id) references bank_account (b_id) --bank id is a foreign key
		on delete cascade
	);
--Creating a publisher
---- gets a percent cut of each sale of their book
create table publisher
	(p_id		integer GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 30000000),
	 b_id		integer,
	 name		varchar(20) not null,
	 address	varchar(30),
	 email		varchar(30) not null,
	 phone		varchar(10),
	 primary key (p_id),
	 foreign key (b_id) references bank_account (b_id)
		on delete cascade
	);
--owns the books in the bookstore
create table owner
	(o_id		integer GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 40000000), 
	 b_id		integer, 
	 name		varchar(20) not null,
	 address	varchar(30),
	 phone		varchar(10),
	 primary key (o_id),
	 foreign key (b_id) references bank_account (b_id)
		on delete cascade
	);
--Table for which owner contacts which publishers
----a publisher can be in contact with 2 owners and an owner can be in contact with 2 publishers
create table contacts
	(o_id		integer,
	 p_id		integer,
	 primary key	(o_id, p_id),
	 foreign key	(o_id) references owner (o_id)
		on delete cascade,
	 foreign key	(p_id) references publisher (p_id)
		on delete cascade
	);
--Reports are generated daily at the end of each day.
----They show expenses vs profit for that day
----They get combined with other tables to generate full reports in the application
create table report
	(date		varchar(8),
	 o_id		integer,
	 expense_amount	numeric(15, 2),
	 profit_amount		numeric(15, 2),
	 primary key (date),
	 foreign key (o_id) references owner (o_id)
		on delete set null
	);
--The book table
create table book
	(ISBN		integer GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1111111111), --10 digit ISBN value like the old standard
	 p_id		integer,
	 o_id		integer,
	 name		varchar(32) not null,--We cant create a book without a name, genre, author, etc..
	 genre		varchar(16) not null,
	 author		varchar(32) not null,
	 pages		numeric(5,0) not null check (pages > 0), --Things like pages, price, inventory must be bigger than 0. 
	 price		numeric(5,2) not null check (price > 0),
	 inventory 	numeric(2,0) not null check (inventory >= 0), 
	 percent_to_publisher	numeric(3,2) not null check (percent_to_publisher >= 0 and percent_to_publisher < 1.00), --percent is a fruction from 0 to 1. 
	 primary key (ISBN),
	 foreign key (o_id) references owner (o_id)
		on delete cascade,
	 foreign key (p_id) references publisher (p_id)
		);
--Keeps track of cutomer order
----an order is created before they place it but the status will be in basket. They have the same order id until they actually place the order
create table c_order
	(order_id	integer GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 50000000), 
	 c_id		integer, 
	 Billing_address	varchar(30) not null DEFAULT '800 Factory Street',
	 shipping_address	varchar(30) not null DEFAULT '800 Factory Street',
	 status			varchar(20) not null DEFAULT 'In Basket',
	 date			varchar(8) not null DEFAULT '00000000',
	 price			numeric(6,2) not null DEFAULT 0,
	 primary key (order_id),
	 foreign key (c_id) references customer (c_id)
		on delete cascade
	);
--Keeps track of all the things in basket.
----The basket is kept even when the castomer logs out zzz
create table book_into_basket
	(order_id	integer,
	 ISBN		integer,
	 amount_sold	numeric(2, 0) not null,
	 primary key (order_id, ISBN),
	 foreign key (order_id) references c_order (order_id)
		on delete cascade,
foreign key (ISBN) references book (ISBN)
	on delete cascade
	);
