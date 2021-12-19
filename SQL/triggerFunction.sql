create function update_profit_fun() returns trigger as $update_profit$
begin 
--The code below find the expense amount from book and book_into_basket
--It sums the price*amount*percent to publisher of all the new book which have been added to an order
	WITH price_sum AS ( 
	SELECT SUM(sums.price*amount_sold*percent_to_publisher) as total
	FROM (SELECT price, amount_sold, percent_to_publisher FROM (book NATURAL JOIN book_into_basket) WHERE order_id = new.order_id) as sums)
--updating expenses with the amount calculated above
	update report
	set expense_amount = expense_amount + price_sum.total from price_sum
	where report.date = NEW.date;
--updating report based on the price of the new order
	update report
	set profit_amount = profit_amount + (select price from c_order where order_id = new.order_id) 
	where report.date = NEW.date;
	
	return new;
end;
$update_profit$ language plpgsql;
-- creating a trigger for the code above
create trigger update_profit after update of price on c_order
for each row execute function update_profit_fun();


create function books_threshold_fun() returns trigger as $books_threshold$
begin
--when inventory is less than 5 then we order more inventory based on books sold in last month
    if (new.inventory < 5) then
    Update book
    set inventory = inventory + 
    --select the amount of books sold based books which have been ordered last month and which match the new ISBN
    (select amount_sold
    from (select ISBN, amount_sold
        From (select CAST(SUBSTRING(date, 3,2) as int) as month, ISBN, amount_sold from
        (Select date, ISBN, amount_sold
         from book_into_basket natural join c_order
		where status!='In Basket') as dates) as s
Where 
    s.month =
    ((SELECT  CAST(SUBSTRING(date, 3,2) as int) as month from
      	--the query below find the latest date and subtracts 1
        (Select date
        From (Select order_id, date
            From c_order 
            where order_id = (select MAX(order_id) from c_order)) as da) as d)-1)) as date
    where new.ISBN = ISBN);
    end if;
	return new;
end;
$books_threshold$ language plpgsql;
--create trigger for the function above
create trigger books_threshold after update of inventory on book
    for each row execute function books_threshold_fun();
