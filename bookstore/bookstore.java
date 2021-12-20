import java.util.Scanner;
import java.sql.*;

public class bookstore {
    String JDBC_DRIVER = "org.postgresql.Driver";
    static final String userid = "postgres";
    static final String passwd = "****"; //password will need to be adjusted

    static int date;

    public static void main(String[] Args){
        Scanner scan = new Scanner(System.in);
        System.out.print("Welcome to the bookstore. Would you like to sign in as: \n1: A customer \n2: An owner \n3: Quit\n");
        String userChoice = "0";
        String userType = null;
        // bootup menu
        while (userChoice.equals("1") != true && userChoice.equals("2") == false && userChoice.equals("3") == false) {
            userChoice = scan.nextLine();
            switch(userChoice) {
                case "1":
                    userType = "customer";
                    break;
                case "2":
                    userType = "owner";
                    break;
                case "3":
                    userType = "quit";
                    break;
            }
        }
        //different ui for customer and owner
        if (userType.equals("customer")) {
            customerUI(scan);
        } else if (userType.equals("owner")) {
            ownerUI(scan);
        }

        scan.close();
    }   

    public static void customerUI(Scanner scan) {
        String userChoice = "0";
        System.out.print("Please enter the customer ID or type 000 to sign-up.\n");
        boolean logged_in = false;
        String c_id = "";
        String order_id = "";
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/COMP3005FinalProject", userid, passwd);
            Statement stmt = conn.createStatement();
            ResultSet rset;
            // While the customer id doesn't match anything in the database, keep requesting an input.
            while (!logged_in) {
                userChoice = scan.nextLine();
                //below registers a customer
                if (userChoice.equals("000")){
                    String name, phone, address; //gets info and inserts into customer table
                    System.out.println("What is your name? ");
                    name = scan.nextLine();
                    System.out.println("What is your address? ");
                    address = scan.nextLine();
                    System.out.println("What is your phone? ");
                    phone = scan.nextLine();
                    stmt.executeUpdate(
                        "insert into bank_account Default Values;" //make a new bank account
                    );
                    rset = stmt.executeQuery (
                        "SELECT max(b_id) FROM BANK_account" //find the id of the newest bank account
                    );
                    rset.next();
                    String bankNumber = rset.getString("max"); //put newest bank account into a var
                    rset = stmt.executeQuery (
                        "SELECT money FROM BANK_account where b_id = "+bankNumber //find money of a bank account
                    );
                    rset.next();
                    String money = rset.getString("money");
                    System.out.println("Your bank account has been linked, your bank account number is " + bankNumber+ ". You have $"+money) ;
                    //make new customer
                    PreparedStatement pStmt = conn.prepareStatement( 
                        "insert into customer values (DEFAULT,?,?,?,?)");
                    //inserting BANK, name, address, phone and updating the new user 
                    pStmt.setInt(1, Integer.parseInt(bankNumber)); //the default bank account
                    pStmt.setString(2, name);
                    pStmt.setString(3, address);
                    pStmt.setString(4, phone);
                    pStmt.executeUpdate();

                    rset = stmt.executeQuery (
                        "SELECT max(c_id) FROM customer" //getting the id to let the user know how to log in to the newly created customer
                    );
                    rset.next();
                    String id = rset.getString("max");
                    System.out.println("Your account has been created. Your login ID is "+ id);
                    c_id = id;
                    logged_in = true;
                }else{
                    System.out.print("Attempting login...\n");
                    rset = stmt.executeQuery (
                        "SELECT c_id FROM customer WHERE c_id = " + userChoice //used to check if user has c_id the customer gave. 
                    ); 
                    if (rset.next()) {
                        if (userChoice.equals(rset.getString("c_id"))) { //If there is a user which that user id then log them in
                            logged_in = true;
                            c_id = userChoice;
                        } 
                    } else { //cant find a user with the id
                        System.out.println("No user match for that id. Try again: ");
                    }
                }
            }

            while (logged_in) {
                rset = stmt.executeQuery (
                    "select max(date) from report as cur_date" //the max date in report will be the current day. This works because a report is generated for each day, and the first report is manually inserted
                );
                if (rset.next()) {
                    date = Integer.parseInt(rset.getString("max"));
                }
                System.out.print("Hello customer! What can we help you with, today?\n1. Search Books, 2. Buy Books, 3. Track an Order, 4. Log out (1, 2, 3, 4) \n"); 
                userChoice = scan.nextLine();
                if (userChoice.equals("1")){
                    while (!userChoice.equals("0")){
                        try{
                            System.out.print("What would you like to search by? 1. ISBN, 2. Name, 3. Genre, 4. Author, 5. Pages, 6. Price (0 to exit): \n");
                            userChoice = scan.nextLine();
                            if (userChoice.equals("0")){ break; }
                            if (userChoice.equals("1")){
                                System.out.print("Please enter the ISBN you would like to search by: ");
                                userChoice = scan.nextLine();
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE ISBN = " + userChoice //get all the book information about an ISBN
                                ); 
                                printBooks(rset);

                            }else if (userChoice.equals("2")){
                                System.out.print("Please enter the Name you would like to search by: ");
                                userChoice = scan.nextLine();
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE Name = " + "'"+userChoice+"'" //get all the book information about a name
                                ); 
                                printBooks(rset);
                            }else if (userChoice.equals("3")){
                                System.out.print("Please enter the Genre you would like to search by: ");
                                userChoice = scan.nextLine();
                                System.out.println(userChoice);
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE Genre = " + "'"+userChoice+"'" //get all the book information about a genre
                                ); 
                                
                                printBooks(rset);
                            }else if (userChoice.equals("4")){
                                System.out.print("Please enter the Author you would like to search by: ");
                                userChoice = scan.nextLine();
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE Author = " + "'"+userChoice+"'" //get all the book information about a author
                                ); 
                                printBooks(rset);
                            }else if (userChoice.equals("5")){
                                System.out.print("Please enter the amount of pages you would like to search by (ex, >300, <200, =333): "); 
                                userChoice = scan.nextLine();
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE Pages" +userChoice //get all the book information about pages can use > or <
                                ); 
                                printBooks(rset);
                            }
                            else if (userChoice.equals("6")){
                                System.out.print("Please enter the price you would like to search by (ex, >20, <30, =19.99): ");
                                userChoice = scan.nextLine();
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE price" + userChoice  //get all the book information about price can use > or <
                                ); 
                                printBooks(rset);
                            }
                        }catch(Exception sqle) {
                        System.out.println("Invalid input. Please try again.");
                        }
                    }
                }else if(userChoice.equals("2")) {
                    rset = stmt.executeQuery(
                        "SELECT order_id, status FROM c_order WHERE c_id = " + c_id + " and status = 'In Basket'" //check to see if the user already has a basket
                    );
                    if (!rset.next()) {
                        stmt.executeUpdate(
                            "INSERT INTO c_order VALUES (default, " + c_id + ", default, default, default, " + date + ", 0)" //if not, create a new order (this order is used to keep track of the user's basket)
                        );
                        rset = stmt.executeQuery(
                            "SELECT order_id, status FROM c_order WHERE c_id = " + c_id + " and status = 'In Basket'" //select the order id of that new basket, to keep track of it
                        );
                        rset.next();
                        order_id = rset.getString("order_id");
                    } else {
                        order_id = rset.getString("order_id");
                        System.out.print("Loading your cart...\n");
                        rset = stmt.executeQuery(
                            "SELECT * FROM (book NATURAL JOIN book_into_basket) WHERE order_id = " + order_id //get information about the books in the basket
                        );
                        printCart(rset);
                    }
                    System.out.print("Please enter a book to add to your cart (using the ISBN): \nOr r to remove from cart, 0 to quit, or o to order what's in your cart.\n");
                    userChoice = scan.nextLine(); 
                    while (!userChoice.equals("0")) {
                        if (userChoice.equals("r")) {
                            System.out.println("Which book would you like to remove? (using the ISBN)");
                            userChoice = scan.nextLine();
                            rset = stmt.executeQuery (
                                "SELECT isbn FROM book_into_basket WHERE isbn = " + userChoice + " and order_id = " + order_id //match books with the user-given isbn
                            );
                            if (rset.next()) {
                                stmt.executeUpdate(
                                    "DELETE FROM book_into_basket WHERE isbn = " + userChoice + " and order_id = " + order_id //delete the book the user selected from their basket
                                );
                            } else {
                                System.out.println("Sorry, but that book isn't currently in your cart.");
                            }
                        } else if (userChoice.equals("o")) {
                            System.out.println("Please enter the billing address.");
                            userChoice = scan.nextLine();
                            stmt.executeUpdate(
                                "UPDATE c_order SET billing_address = '" + userChoice + "' WHERE order_id = " + order_id //update the billing address
                            );
                            System.out.println("Please enter the delivery address.");
                            userChoice = scan.nextLine();
                            stmt.executeUpdate(
                                "UPDATE c_order SET shipping_address = '" + userChoice + "' WHERE order_id = " + order_id //update the shipping address
                            );
                            stmt.executeUpdate (//update the price of the order from the calculation done below
                                "WITH price_sum AS (SELECT SUM(sums.price*amount_sold) as total FROM (SELECT price, amount_sold FROM (book NATURAL JOIN book_into_basket) WHERE order_id = " + order_id +") as sums) UPDATE c_order SET price = price_sum.total from price_sum WHERE order_id = " + order_id
                            );
                            stmt.executeUpdate ( //update the inventory of the order
                                "WITH books AS (SELECT isbn, amount_sold FROM book_into_basket WHERE order_id = " + order_id + ") UPDATE book set inventory = inventory - amount_sold FROM books WHERE books.isbn = book.isbn"
                            );
                            stmt.executeUpdate (//change the status of the order 
                                "UPDATE c_order SET status = 'Pending' WHERE order_id = " + order_id
                            );
                            System.out.println("Your order is being shipped!");
                            System.out.println("Your order number is "+ order_id);
                            stmt.executeUpdate(
                                "INSERT INTO c_order VALUES (default, " + c_id + ", default, default, default, " + date +", 0)" //make a new order (this order is used as a basket, not a complete order)
                            );
                            rset = stmt.executeQuery(
                                "SELECT order_id, status FROM c_order WHERE c_id = " + c_id + " and status = 'In Basket'" //get the id of this new order (it's generated by the database) to store it
                            );
                            rset.next();
                            order_id = rset.getString("order_id");
                        } else {
                            rset = stmt.executeQuery (
                                "SELECT isbn, name, author, price, inventory FROM book WHERE isbn = " + userChoice //match this isbn to the database
                            );
                            if (rset.next()) { // if it matches...
                                if (userChoice.equals(rset.getString("isbn"))) { //print info about the book
                                    System.out.print("There are " + rset.getString("inventory") + " copies of " + rset.getString("name") + " by " + rset.getString("author") + " in stock.\n"
                                    + "The price per copy is $" + rset.getString("price") + ".\n" + "How many would you like to order?\n");
                                    userChoice = scan.nextLine();
                                    
                                    if (Integer.parseInt(rset.getString("inventory")) >= Integer.parseInt(userChoice) && Integer.parseInt(userChoice) > 0) {//between 0 and the total number of that book in the database
                                        stmt.executeUpdate(
                                            "INSERT INTO book_into_basket VALUES (" + order_id + "," + rset.getString("isbn") + "," + userChoice + ")" //insert into basket the book the user wants to buy, their order number, and amount of that book
                                        );
                                    } else {
                                        System.out.println("That number isn't valid.");
                                    }
                                }
                            }
                        }
                        rset = stmt.executeQuery(
                            "SELECT * FROM (book NATURAL JOIN book_into_basket) WHERE order_id = " + order_id //select cart information for printCart()
                        );
                        System.out.print("Loading your cart...\n");
                        printCart(rset);
                        System.out.print("Please enter a book to add to your cart (using the ISBN): \nOr r to remove from cart, 0 to quit, or o to order what's in your cart.\n");
                        userChoice = scan.nextLine();
                    }
                }else if (userChoice.equals("3")){
                    while (!userChoice.equals("0")){
                        try{
                            System.out.print("Please type the order number you would like to track (0 to exit): ");
                            userChoice = scan.nextLine();
                            if (userChoice.equals("0")){ break; }
                            
                            rset = stmt.executeQuery (
                                    "SELECT Status, shipping_address FROM c_order WHERE order_id = " + userChoice //get the status and shipping address of an order
                                ); 
                            
                            rset.next();
                            String status = rset.getString("status");
                            String shipping_address = rset.getString("shipping_address");
                            System.out.println ("Your order is         "+ status +"\nIt is on the way to   "+ shipping_address);
                            rset = stmt.executeQuery (
                                "SELECT Status, shipping_address FROM c_order WHERE order_id = " + userChoice //get the status and shipping address from an order
                            );
                        }catch(Exception sqle) {
                            System.out.println("You entered wrong input!");
                        }
                    }
                }else if (userChoice.equals("4")){
                    logged_in = false;
                }
            }
        } catch (Exception sqle) {
            System.out.println("Exception : " + sqle);
        }

        

    }
    public static void printBooks(ResultSet rset) {
        try {
            String s = String.format("    %-20s %-35s %-20s %-20s %-9s %-8s", "ISBN","Name", "Genre","Author","Pages","Price");
                System.out.println(s);
            while (rset.next()) {
                printBook(rset);
            }
        } catch (Exception sqle) {
            System.out.println("Exception : " + sqle);
        }
        System.out.println();
    }

    public static void printBook(ResultSet rset) {
        try {
                String ss = String.format("    %-20s %-35s %-20s %-20s %-9s %-8s", rset.getString("ISBN"),rset.getString("name"), rset.getString("Genre"),rset.getString("Author"),rset.getString("Pages"),rset.getString("price"));
                System.out.println(ss);
        } catch (Exception sqle) {
            System.out.println("Exception : " + sqle);
        }
    }

    public static void printCart(ResultSet rset) {
        try {
        String s = String.format("    %-20s %-35s %-20s %-20s %-9s %-8s", "ISBN","Name", "Genre","Author","Pages","Price");
                System.out.println(s);
            while (rset.next()) {
                printBook(rset);
                System.out.println("You have " + rset.getString("amount_sold") + " copies of this book in your cart.");
            }
        } catch (Exception sqle) {
            System.out.println("Exception : " + sqle);
        }
        System.out.println();

    }

    // Loop covering options for the owner
    public static void ownerUI(Scanner scan) {
        String userChoice = "0";
        System.out.print("Please enter the owner ID: ");
        
        boolean logged_in = false;
        String o_id = "";

        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/COMP3005FinalProject", userid, passwd);
            Statement stmt = conn.createStatement();
            ResultSet rset;
            // While the owner id doesn't match anything in the database, keep requesting an input.
            while (!logged_in) {
                userChoice = scan.nextLine();
                
                System.out.print("Attempting login...\n");
                rset = stmt.executeQuery (
                    "SELECT o_id FROM owner WHERE o_id = " + userChoice //select the id of an owner matching the users choice
                ); 
                if (rset.next()) {
                    if (userChoice.equals(rset.getString("o_id"))) {
                        logged_in = true;
                        o_id = userChoice;

                    } 
                } else {
                    System.out.println("No user match for that id. Try again: ");
                }
            }
            while (logged_in) {
                rset = stmt.executeQuery (
                    "select max(date) from report as cur_date" //find the latest date 
                );
                if (rset.next()) {
                    date = Integer.parseInt(rset.getString("max")); 
                }
                System.out.print("Hello owner! What can we help you with, today?\n1. Add New Books 2. Remove Books, 3. Generate Reports 4. Change Date 5. Log-Out (1, 2, 3, 4, 5) \n");
                userChoice = scan.nextLine();
                if (userChoice.equals("1")){
                    while (!userChoice.equals("0")){
                        try{
                            System.out.print ("Please enter the ISBN of the book you would like to add (1 to create new book, 0 to exit) :");
                            userChoice = scan.nextLine();
                            if (userChoice.equals("0")) {break;}
                            System.out.print ("How many orders of "+userChoice + " would you like to order (1 = 1 copy) ");
                            int bookNum = Integer.parseInt(scan.nextLine());
                        
                            rset = stmt.executeQuery (
                                "SELECT * FROM book WHERE ISBN = " + userChoice //match isbn to the database
                            ); 
                            
                            String owner = "";
                            int inventory = 0;
                            if (rset.next()) {
                                owner = rset.getString("o_id");
                                inventory = Integer.parseInt(rset.getString("inventory"));
                                if (owner.equals(o_id)){
                                    System.out.println("You own this book in the bookstore. " + bookNum + " more of this book will be ordered.");
                                    PreparedStatement pStmt = conn.prepareStatement("update book set inventory = ? where ISBN = ?"); // increase the inventory for this book
                                    pStmt.setInt (1, (inventory+bookNum));
                                    pStmt.setInt (2, Integer.parseInt(userChoice));
                                    pStmt.executeUpdate();
                                }else if (!owner.equals(o_id)) {
                                    System.out.println("This book already has an exclusive owner, you cannot order this book");
                                }
                            }
                            else {
                                System.out.println("This book does not have an owner, you will become the owner and "+ bookNum + " books will be ordered.");
                                System.out.print("What is the name of this book?: ");
                                String name = scan.nextLine();
                                System.out.print("What is the genre of this book?: ");
                                String genre = scan.nextLine();
                                System.out.print("Who is the author of this book?: ");
                                String author = scan.nextLine();
                                System.out.print("How many pages does this book have?: ");
                                int pages = Integer.parseInt(scan.nextLine());
                                System.out.print("What would you like the price of this book to be?: ");
                                Double price = Double.parseDouble(scan.nextLine());
                                System.out.print("What percent does the publisher take per sale? (ex. 0.05 = 5%): ");
                                Double percent = Double.parseDouble(scan.nextLine());
                                System.out.print("What is the publisher id of "+name+"?: ");
                                String publisher = scan.nextLine();

                                rset = stmt.executeQuery (
                                    "SELECT p_id FROM publisher WHERE p_id = " + publisher
                                ); 
                                if (rset.next() == false) {
                                    System.out.println("The publisher you have entered does not exist in the publishers we are partners with. Please try again");
                                    continue;
                                }
                                PreparedStatement pStmt = conn.prepareStatement(
                                "insert into book values (DEFAULT,?,?,?,?,?,?,?,?,?)"); //insert a new book
                                pStmt.setInt(1, Integer.parseInt(publisher));
                                pStmt.setInt(2, Integer.parseInt(o_id));
                                pStmt.setString(3, name);
                                pStmt.setString(4, genre);
                                pStmt.setString(5, author);
                                pStmt.setInt(6, pages);
                                pStmt.setDouble(7, price);
                                pStmt.setInt(8, bookNum);
                                pStmt.setDouble(9, percent);
                                pStmt.executeUpdate();
                                System.out.println(bookNum + "Copies of this book have been added to the inventory.");
                                System.out.println("Customers can now buy " + name +" for $" + price);

                                rset = stmt.executeQuery (
                                    "SELECT p_id FROM contacts WHERE p_id = " + publisher + "and o_id = " + o_id //select the publisher id where owner id is the one the user gave and the publisher is the publisher of the book
                                ); 
                                if (rset.next() == false) { //if the owner and publisher are not in contact, add them as a new contact relationship
                                    System.out.println("This is your first book from this publisher, we will add the publisher to your contacts");
                                    PreparedStatement pStmt1 = conn.prepareStatement(
                                    "insert into contacts values (?,?)"); //insert into contacts. AKA make the publisher and owner be in contact
                                    pStmt1.setInt(1, Integer.parseInt(publisher));
                                    pStmt1.setInt(2, Integer.parseInt(o_id));
                                    pStmt.executeUpdate();
                                }
                            }
                        }catch (Exception sqle) {
                           System.out.println("Exception : " + sqle);
                        }
                    }
                }else if (userChoice.equals("2")){
                    while (!userChoice.equals("0")) {
                        rset = stmt.executeQuery (
                            "SELECT * FROM book WHERE o_id = " + o_id //select all of the books belonging to this owner
                        );
                        System.out.println("Here are all of your books.");
                        printBooks(rset);
                        System.out.println("Please choose which book you'd like to remove from the store (Using the ISBN) or 0 to go back.");
                        userChoice = scan.nextLine();
                        if (!userChoice.equals("0")) {
                            rset = stmt.executeQuery (
                                "SELECT * FROM book WHERE isbn = " + userChoice + " AND o_id = " + o_id //match the isbn with the subset of books owned by this owner
                            );
                            if (rset.next()) { //if the book the user selected exists, they can remove it (can only remove books they own)
                                stmt.executeUpdate (
                                    "DELETE FROM book WHERE isbn = " + userChoice + " AND o_id = " + o_id //delete the book with isbn the user gave and owner id
                                );
                                System.out.println("Book has been removed.");
                            } else {
                                System.out.println("That book doesn't exist, or you don't have any copies of it.");
                            }
                        }
                    }
                }else if (userChoice.equals("3")){
                    while (!userChoice.equals("0")){
                        try{
                            
                            System.out.print ("For which date would you like to generate the report? (as YYYYMMDD ex 20210413, 0 to exit) ");
                            userChoice = scan.nextLine();
                            if (userChoice.equals("0")){ break; }
                            System.out.println("\nGenerating reports for "+userChoice + "...");
                            rset = stmt.executeQuery (
                                "select expense_amount, profit_amount from report where date = " + "'"+userChoice+"'" //get the expenses and profit from the report
                            ); 
                            
                            String s = String.format("    %-35s %-20s", "Expense Amount", "Profit Amount");
                            System.out.println(s);
                            while (rset.next()) {
                                String ss = String.format("    %-35s %-20s", rset.getString("expense_amount"),rset.getString("profit_amount"));
                                System.out.println(ss);
                            } 
                            System.out.println(); 
                            rset = stmt.executeQuery ( //get the sales per genre
                                "select genre, sum (amount_sold) from report, c_order, book_into_basket, book where report.date = c_order.date and book_into_basket.order_id = c_order.order_id and book_into_basket.ISBN = book.ISBN and report.date = " + "'"+userChoice+"'"+"group by genre"
                            ); 
                             s = String.format("    %-35s %-20s", "Genre", "Amount Sold");
                            System.out.println(s);
                            while (rset.next()) {
                                String ss = String.format("    %-35s %-20s", rset.getString("genre"),rset.getString("sum"));
                                System.out.println(ss);
                            } 
                            System.out.println(); 
                            rset = stmt.executeQuery (//get the sales per author
                                "select author, sum (amount_sold) from report, c_order, book_into_basket, book where report.date = c_order.date and book_into_basket.order_id = c_order.order_id and book_into_basket.ISBN = book.ISBN and report.date = " + "'"+userChoice+"'"+"group by author"
                            ); 
                             s = String.format("    %-35s %-20s", "Author", "Amount Sold");
                            System.out.println(s);
                            while (rset.next()) {
                                String ss = String.format("    %-35s %-20s", rset.getString("author"),rset.getString("sum"));
                                System.out.println(ss);
                            } 

                            System.out.println(); 
                            rset = stmt.executeQuery (//get the sales per name
                                "select name, sum (amount_sold) from report, c_order, book_into_basket, book where report.date = c_order.date and book_into_basket.order_id = c_order.order_id and book_into_basket.ISBN = book.ISBN and report.date = " + "'"+userChoice+"'"+"group by name"
                            ); 
                             s = String.format("    %-35s %-20s", "Name", "Amount Sold");
                            System.out.println(s);
                            while (rset.next()) {
                                String ss = String.format("    %-35s %-20s", rset.getString("name"),rset.getString("sum"));
                                System.out.println(ss);
                            }

                            System.out.println(); 
                            rset = stmt.executeQuery (//get the sales per publisher
                                "select p_id, sum (amount_sold) from report, c_order, book_into_basket, book where report.date = c_order.date and book_into_basket.order_id = c_order.order_id and book_into_basket.ISBN = book.ISBN and report.date = " + "'"+userChoice+"'"+"group by p_id"
                            ); 
                             s = String.format("    %-35s %-20s", "Publisher ID", "Amount Sold");
                            System.out.println(s);
                            while (rset.next()) {
                                String ss = String.format("    %-35s %-20s", rset.getString("p_id"),rset.getString("sum"));
                                System.out.println(ss);
                            }


                            
                            
                
                        }catch (Exception sqle) {
                           System.out.println("Exception : " + sqle);
                        }
                    }
                }else if (userChoice.equals("4")) {
                    System.out.println("The current date is:" + date + " (YYYYMMDD).");
                    System.out.println("0. Go back. 1. Go to the next day. 2. Go to the next month (30 days).");
                    String dateS;
                    userChoice = scan.nextLine();
                    while (!userChoice.equals("0")) {
                        if (userChoice.equals("1")) {
                            dateS = String.valueOf(date);
                            pay_publishers(conn, stmt, rset);
                            time_travel();
                            stmt.executeUpdate(
                                "UPDATE c_order SET status = 'Delivered' WHERE status = 'Shipped'" //update status of order
                            );
                            stmt.executeUpdate(
                                "UPDATE c_order SET status = 'Shipped' WHERE status = 'Pending'" //update status of order
                            );
                            stmt.executeUpdate (
                                "INSERT INTO report VALUES (" + date + ", " + o_id + ", 0, 0)" //insert a new report at the end of each day, calculates the amount in sql
                            );
                            
                            if (dateS.substring(6, 8).equals("01")) {
                                stmt.executeUpdate(
                                    "UPDATE report SET expense_amount = expense_amount + 100 WHERE date = '" + date + "'" //update the report buy adding rent an expense
                                );
                            }
                        } else if (userChoice.equals("2")) {
                            for (int i = 0; i < 31; ++i) {
                                pay_publishers(conn, stmt, rset);
                                time_travel();
                                stmt.executeUpdate (
                                    "INSERT INTO report VALUES (" + date + ", " + o_id + ", 0, 0)"
                                );
                                stmt.executeUpdate(
                                    "UPDATE c_order SET status = 'Delivered' WHERE status = 'Shipped'"//update status of order
                                );
                                stmt.executeUpdate(
                                    "UPDATE c_order SET status = 'Shipped' WHERE status = 'Pending'"//update status of order
                                );
                                dateS = String.valueOf(date);
                                if (dateS.substring(6, 8).equals("01")) {
                                    stmt.executeUpdate(
                                        "UPDATE report SET expense_amount = expense_amount + 100 WHERE date = '" + date + "'" //add rent amount to expenses
                                    );
                                }
                            }
                        }
                        System.out.println("The current date is:" + date + " (YYYYMMDD).");
                        System.out.println("0. Go back. 1. Go to the next day. 2. Go to the next month (30 days).");
                        userChoice = scan.nextLine();
                    }
                }else if (userChoice.equals("5")){
                    System.out.println("Logging out...");
                    logged_in = false;
                }
            }

         
         
         

         
        } catch (Exception sqle) {
            System.out.println("Exception : " + sqle);
        }
    }
    public static void pay_publishers(Connection conn, Statement stmt, ResultSet rset) {
        try {
            String dateS = String.valueOf(date);
            rset = stmt.executeQuery (//calculate the amount to pay publishers based on sales that day (not taken from expenses in report because of rent calculations)
                "select amount_sold * book.price*book.percent_to_publisher as price, publisher.p_id, publisher.b_id from book_into_basket, c_order, book, publisher where c_order.order_id = book_into_basket.order_id and book_into_basket.ISBN = book.ISBN and publisher.p_id = book.p_id and date = '"+ dateS + "'"
            ); 
            while (rset.next()) {
                Statement stmt1 = conn.createStatement();
                double price = rset.getDouble("price");
                int b_id = rset.getInt("b_id");
                stmt1.executeUpdate(
                "update bank_account set money = money + " + price + "where bank_account.b_id = " + b_id //update the amount to pay publisher with the amount found in the query above
                );
            }
        }catch(Exception sqle) {
            System.out.println("Exception : " + sqle);
        }
    }

    public static void time_travel() {
        ++date;
        String dateS = String.valueOf(date);
        if (dateS.substring(4, 6).equals("01")||dateS.substring(4, 6).equals("03")||dateS.substring(4, 6).equals("05")||dateS.substring(4, 6).equals("07")||dateS.substring(4, 6).equals("08")||dateS.substring(4, 6).equals("10")||dateS.substring(4, 6).equals("12")) {
            if (dateS.substring(6, 8).equals("32")) {
                date -= 31;
                date += 100;
            }
        } else if (dateS.substring(4, 6).equals("02")) {
            if (dateS.substring(6, 8).equals("29")) {
                date -= 28;
                date += 100;
            }
        } else if (dateS.substring(4, 6).equals("04")||dateS.substring(4, 6).equals("06")||dateS.substring(4, 6).equals("09")||dateS.substring(4, 6).equals("11")) {
            if (dateS.substring(6, 8).equals("31")) {
                date -= 30;
                date += 100;
            }
        }
        dateS = String.valueOf(date);
        if (dateS.substring(4, 6).equals("13")) {
            date -= 1200;
            date += 10000;
        }
    }
}
