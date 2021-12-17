import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class bookstore {
    String JDBC_DRIVER = "org.postgresql.Driver";
    static final String userid = "postgres";
    static final String passwd = "Ta150555";
    
    public static void main(String[] Args){
        Scanner scan = new Scanner(System.in);
        System.out.print("Welcome to the bookstore. Would you like to sign in as: \n1: A customer \n2: An owner \n3: Quit\n");
        String userChoice = "0";
        String userType = null;
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

        
        if (userType.equals("customer")) {
            customerUI(scan);
        } else if (userType.equals("owner")) {
            ownerUI(scan);
        }

        scan.close();
    }   

    public static void customerUI(Scanner scan) {
        String userChoice = "0";
        System.out.print("Please enter the customer ID or type 000 to sign-up .\n");
        boolean logged_in = false;
        String c_id = "";
        String order_id = "";
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/Final_Project", userid, passwd);
            Statement stmt = conn.createStatement();
            ResultSet rset;
            // While the customer id doesn't match anything in the database, keep requesting an input.
            while (!logged_in) {
                userChoice = scan.nextLine();
                if (userChoice.equals("000")){
                    String name, phone, address;
                    System.out.println("What is your name? ");
                    name = scan.nextLine();
                    System.out.println("What is your address? ");
                    address = scan.nextLine();
                    System.out.println("What is your phone? ");
                    phone = scan.nextLine();
                    stmt.executeUpdate(
                        "insert into bank_account Default Values;"
                    );
                    rset = stmt.executeQuery (
                        "SELECT max(b_id) FROM BANK_account"
                    );
                    rset.next();
                    String bankNumber = rset.getString("max");
                    rset = stmt.executeQuery (
                        "SELECT money FROM BANK_account where b_id = "+bankNumber
                    );
                    rset.next();
                    //int bn = Integer.parseInt(bankNumber);
                    String money = rset.getString("money");
                    System.out.println("You bank account has been links, you bank acccount number is " + bankNumber+ " You have $"+money) ;
                    PreparedStatement pStmt = conn.prepareStatement(
                        "insert into customer values (DEFAULT,?,?,?,?)");
                    
                    pStmt.setInt(1, Integer.parseInt(bankNumber));
                    pStmt.setString(2, name);
                    pStmt.setString(3, address);
                    pStmt.setString(4, phone);
                    pStmt.executeUpdate();

                    rset = stmt.executeQuery (
                        "SELECT max(c_id) FROM customer"
                    );
                    rset.next();
                    String id = rset.getString("max");
                    System.out.println("Your new user has been creates. You login is "+ id);
                    c_id = id;
                    logged_in = true;
                }else{
                    System.out.print("Attempting login...\n");
                    rset = stmt.executeQuery (
                        "SELECT c_id FROM customer WHERE c_id = " + userChoice
                    ); 
                    if (rset.next()) {
                        if (userChoice.equals(rset.getString("c_id"))) {
                            logged_in = true;
                            c_id = userChoice;

                        } 
                    } else {
                        System.out.println("No user match for that id. Try again: ");
                    }
                }
            }

            while (logged_in) {
                System.out.print("Hello customer! What can we help you with, today?\n 1. Search Books, 2. Buy Books, 3. Tracking order , 4. Log-Out (1, 2, 3, 4) ");
                userChoice = scan.nextLine();
                if (userChoice.equals("1")){
                    while (!userChoice.equals("0")){
                        try{
                            System.out.print("What would you like to search by? 1. ISBN, 2. Name, 3. Genre, 4. Author, 5. Pages , 6. Price (0 to exit): ");
                            userChoice = scan.nextLine();
                            if (userChoice.equals("0")){ break; }
                            if (userChoice.equals("1")){
                                System.out.print("Please enter the ISBN you would like to search for: ");
                                userChoice = scan.nextLine();
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE ISBN = " + userChoice
                                ); 
                                printBooks(rset);

                            }else if (userChoice.equals("2")){
                                System.out.print("Please enter the Name you would like to search for: ");
                                userChoice = scan.nextLine();
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE Name = " + "'"+userChoice+"'"
                                ); 
                                printBooks(rset);
                            }else if (userChoice.equals("3")){
                                System.out.print("Please enter the Genre you would like to search for: ");
                                userChoice = scan.nextLine();
                                System.out.println(userChoice);
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE Genre = " + "'"+userChoice+"'"
                                ); 
                                
                                printBooks(rset);
                            }else if (userChoice.equals("4")){
                                System.out.print("Please enter the Author you would like to search for: ");
                                userChoice = scan.nextLine();
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE Author = " + "'"+userChoice+"'"
                                ); 
                                printBooks(rset);
                            }else if (userChoice.equals("5")){
                                System.out.print("Please enter the amount of pages you would like to search for (ex, >300, <200, =333): ");
                                userChoice = scan.nextLine();
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE Pages" +userChoice
                                ); 
                                printBooks(rset);
                            }
                            else if (userChoice.equals("6")){
                                System.out.print("Please enter the price you would like to search for (ex, >20, <30, =19.99): ");
                                userChoice = scan.nextLine();
                                rset = stmt.executeQuery (
                                    "SELECT * FROM book WHERE price" + userChoice
                                ); 
                                printBooks(rset);
                            }
                        }catch(Exception sqle) {
                        System.out.println("You entered wrong input!");
                        }
                    }
                }else if(userChoice.equals("2")) {
                    rset = stmt.executeQuery(
                        "SELECT order_id, status FROM c_order WHERE c_id = " + c_id + " and status = 'In Basket'"
                    );
                    if (!rset.next()) {
                        stmt.executeUpdate(
                            "INSERT INTO c_order VALUES (default, " + c_id + ", default, default, default, 20042021, 0)"
                        );
                        rset = stmt.executeQuery(
                        "SELECT order_id, status FROM c_order WHERE c_id = " + c_id + " and status = 'In Basket'"
                        );
                        rset.next();
                        order_id = rset.getString("order_id");
                    } else {
                        order_id = rset.getString("order_id");
                        System.out.print("Loading your cart...\n");
                        rset = stmt.executeQuery(
                            "SELECT * FROM (book NATURAL JOIN book_into_basket) WHERE order_id = " + order_id
                        );
                        printCart(rset);
                    }
                    System.out.print("Please enter a book to add to your cart (using the ISBN): \nOr r to remove from cart, q to quit, or o to order what's in your cart.\n");
                    userChoice = scan.nextLine(); // Have one first to detect quit
                    while (!userChoice.equals("q")) {
                        if (userChoice.equals("r")) {
                            System.out.println("Which book would you like to remove? (using the ISBN)");
                            userChoice = scan.nextLine();
                            rset = stmt.executeQuery (
                                "SELECT isbn FROM book_into_basket WHERE isbn = " + userChoice + " and order_id = " + order_id
                            );
                            if (rset.next()) {
                                stmt.executeUpdate(
                                    "DELETE FROM book_into_basket WHERE isbn = " + userChoice + " and order_id = " + order_id
                                );
                            } else {
                                System.out.println("Sorry, but that book isn't currently in your cart.");
                            }
                        } else if (userChoice.equals("o")) {
                            System.out.println("Please enter the billing address.");
                            userChoice = scan.nextLine();
                            stmt.executeUpdate(
                                "UPDATE c_order SET billing_address = '" + userChoice + "' WHERE order_id = " + order_id 
                            );
                            System.out.println("Please enter the delivery address.");
                            userChoice = scan.nextLine();
                            stmt.executeUpdate(
                                "UPDATE c_order SET shipping_address = '" + userChoice + "' WHERE order_id = " + order_id 
                            );
                            stmt.executeUpdate (
                                "WITH price_sum AS (SELECT SUM(sums.price*amount_sold) as total FROM (SELECT price, amount_sold FROM (book NATURAL JOIN book_into_basket) WHERE order_id = " + order_id +") as sums) UPDATE c_order SET price = price_sum.total from price_sum WHERE order_id = " + order_id
                            );
                            stmt.executeUpdate (
                                "UPDATE c_order SET status = 'Shipped' WHERE order_id = " + order_id
                            );
                            System.out.println("Your order has been shipped!");
                            stmt.executeUpdate(
                                "INSERT INTO c_order VALUES (default, " + c_id + ", default, default, default, 20042021, 0)"
                            );
                            rset = stmt.executeQuery(
                                "SELECT order_id, status FROM c_order WHERE c_id = " + c_id + " and status = 'In Basket'"
                                );
                            rset.next();
                            order_id = rset.getString("order_id");
                        } else {
                            rset = stmt.executeQuery (
                                "SELECT isbn, name, author, price, inventory FROM book WHERE isbn = " + userChoice
                            );
                            if (rset.next()) {
                                if (userChoice.equals(rset.getString("isbn"))) {
                                    System.out.print("There are " + rset.getString("inventory") + " copies of " + rset.getString("name") + " by " + rset.getString("author") + " in stock.\n"
                                    + "The price per copy is $" + rset.getString("price") + ".\n" + "How many would you like to order?\n");
                                    userChoice = scan.nextLine();
                                    if (Integer.parseInt(userChoice) > 0) {
                                        stmt.executeUpdate(
                                            "INSERT INTO book_into_basket VALUES (" + order_id + "," + rset.getString("isbn") + "," + userChoice + ")"
                                        );
                                    }
                                }
                            }
                        }
                        rset = stmt.executeQuery(
                            "SELECT * FROM (book NATURAL JOIN book_into_basket) WHERE order_id = " + order_id
                        );
                        System.out.print("Loading your cart...\n");
                        printCart(rset);
                        System.out.print("Please enter a book to add to your cart (using the ISBN): \nOr r to remove from cart, q to quit, or o to order what's in your cart.\n");
                        userChoice = scan.nextLine();
                        }
                    }else if (userChoice.equals("3")){
                    while (!userChoice.equals("0")){
                        try{
                            System.out.print("Please type the order number you would like to track (0 to exit): ");
                            userChoice = scan.nextLine();
                            if (userChoice.equals("0")){ break; }
                            
                            rset = stmt.executeQuery (
                                    "SELECT Status, shipping_address FROM c_order WHERE order_id = " + userChoice
                                ); 
                            
                            rset.next();
                            String status = rset.getString("status");
                            String shipping_address = rset.getString("status");
                            System.out.println ("Your order is         "+ status +"\nIt is on the way to   "+shipping_address);
                            rset = stmt.executeQuery (
                                "SELECT Status, shipping_address FROM c_order WHERE order_id = " + userChoice
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
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/Final_Project", userid, passwd);
            Statement stmt = conn.createStatement();
            ResultSet rset;
            // While the customer id doesn't match anything in the database, keep requesting an input.
            while (!logged_in) {
                userChoice = scan.nextLine();
                
                System.out.print("Attempting login...\n");
                rset = stmt.executeQuery (
                    "SELECT o_id FROM owner WHERE o_id = " + userChoice
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
                System.out.print("Hello owner! What can we help you with, today?\n 1. Add New Books 2. Remove Books, 3. Generate Reports 4. Log-Out (1, 2, 3, 4) ");
                userChoice = scan.nextLine();
                if (userChoice.equals("1")){
                    while (!userChoice.equals("0")){
                        try{
                            System.out.print ("Please enter the ISBN of the book you would like to add (1 to create new book, 0 to exit) ");
                            userChoice = scan.nextLine();
                            if (userChoice.equals("0")) {break;}
                            System.out.print ("How many orders of "+userChoice + " would you like to order (1=new book) ");
                            int bookNum = Integer.parseInt(scan.nextLine());
                        
                            rset = stmt.executeQuery (
                                "SELECT * FROM book WHERE ISBN = " + userChoice
                            ); 
                            
                            String owner = "";
                            int inventory = 0;
                            if (rset.next()) {
                                owner = rset.getString("o_id");
                                inventory = Integer.parseInt(rset.getString("inventory"));
                                if (owner.equals(o_id)){
                                    System.out.println("You own this book in the bookstore. " + bookNum + " more of this book will be ordered");
                                    PreparedStatement pStmt = conn.prepareStatement("update book set inventory = ? where ISBN = ?");
                                    pStmt.setInt (1, (inventory+bookNum));
                                    pStmt.setInt (2, Integer.parseInt(userChoice));
                                    pStmt.executeUpdate();
                                }else if (!owner.equals(o_id)) {
                                    System.out.println("This book already has an exclusive owner, you cannot order this book");
                                }
                            }
                            else {
                                System.out.println("This book does not have an owner, you will become the owner and "+ bookNum + " books will be orders");
                                System.out.print("What is the name of this book: ");
                                String name = scan.nextLine();
                                System.out.print("What is the genre of this book: ");
                                String genre = scan.nextLine();
                                System.out.print("Who is the author of this book: ");
                                String author = scan.nextLine();
                                System.out.print("How many pages in book: ");
                                int pages = Integer.parseInt(scan.nextLine());
                                System.out.print("What would you like the price of book: ");
                                Double price = Double.parseDouble(scan.nextLine());
                                System.out.print("How much percent does the publisher take for this book: ");
                                Double percent = Double.parseDouble(scan.nextLine());
                                System.out.print("What is the publisher id of "+name+": ");
                                String publisher = scan.nextLine();

                                rset = stmt.executeQuery (
                                    "SELECT p_id FROM publisher WHERE p_id = " + publisher
                                ); 
                                if (rset.next() == false) {
                                    System.out.println("The publisher you have entered does not exist in the publishers we are partners with. Please try again");
                                    continue;
                                }
                                PreparedStatement pStmt = conn.prepareStatement(
                                "insert into book values (DEFAULT,?,?,?,?,?,?,?,?,?)");
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
                                System.out.println("The book has been added to the book database.");
                                System.out.println("Customers can now buy " + name +" for $" + price);

                                rset = stmt.executeQuery (
                                    "SELECT p_id FROM contacts WHERE p_id = " + publisher + "and o_id = " + o_id
                                ); 
                                if (rset.next() == false) {
                                    System.out.println("This is your first book from this publisher, we will add the publisher to your contacts");
                                    PreparedStatement pStmt1 = conn.prepareStatement(
                                    "insert into contacts values (?,?)");
                                    pStmt1.setInt(2, Integer.parseInt(publisher));
                                    pStmt1.setInt(1, Integer.parseInt(o_id));
                                    pStmt1.executeUpdate();
                                }
                            }
                        }catch (Exception sqle) {
                           System.out.println("Exception : " + sqle);
                        }
                    }
                }else if (userChoice.equals("2")){
                    while (!userChoice.equals("0")) {
                        rset = stmt.executeQuery (
                            "SELECT * FROM book WHERE o_id = " + o_id
                        );
                        System.out.println("Here are all of your books.");
                        printBooks(rset);
                        System.out.println("Please choose which book you'd like to remove from the store (Using the ISBN) or 0 to go back.");
                        userChoice = scan.nextLine();
                        if (!userChoice.equals("0")) {
                            rset = stmt.executeQuery (
                                "SELECT * FROM book WHERE isbn = " + userChoice + " AND o_id = " + o_id
                            );
                            if (rset.next()) {
                                stmt.executeUpdate (
                                    "DELETE FROM book WHERE isbn = " + userChoice + " AND o_id = " + o_id
                                );
                                System.out.println("Book has been removed.");
                            } else {
                                System.out.println("That book doesn't exist, or you don't have any copies of it.");
                            }
                        }
                    }
                }
                else if (userChoice.equals("3")){
                    while (!userChoice.equals("0")){
                        try{
                            
                            System.out.print ("For which date would you like to generate the report? (year,month,day ex 20210413 , 0 to exit) ");
                            userChoice = scan.nextLine();
                            if (userChoice.equals("0")){ break; }
                            System.out.println("\nGenerating reports for "+userChoice + "...");
                            rset = stmt.executeQuery (
                                "select expense_amount, profit_amount from report where date = " + "'"+userChoice+"'"
                            ); 
                            
                            String s = String.format("    %-35s %-20s", "Expense Amount", "Profit Amount");
                            System.out.println(s);
                            while (rset.next()) {
                                String ss = String.format("    %-35s %-20s", rset.getString("expense_amount"),rset.getString("profit_amount"));
                                System.out.println(ss);
                            } 
                            System.out.println(); 
                            rset = stmt.executeQuery (
                                "select genre, sum (amount_sold) from report, c_order, book_into_basket, book where report.date = c_order.date and book_into_basket.order_id = c_order.order_id and book_into_basket.ISBN = book.ISBN and report.date = " + "'"+userChoice+"'"+"group by genre"
                            ); 
                             s = String.format("    %-35s %-20s", "Genre", "Amount Sold");
                            System.out.println(s);
                            while (rset.next()) {
                                String ss = String.format("    %-35s %-20s", rset.getString("genre"),rset.getString("sum"));
                                System.out.println(ss);
                            } 
                            System.out.println(); 
                            rset = stmt.executeQuery (
                                "select author, sum (amount_sold) from report, c_order, book_into_basket, book where report.date = c_order.date and book_into_basket.order_id = c_order.order_id and book_into_basket.ISBN = book.ISBN and report.date = " + "'"+userChoice+"'"+"group by author"
                            ); 
                             s = String.format("    %-35s %-20s", "author", "Amount Sold");
                            System.out.println(s);
                            while (rset.next()) {
                                String ss = String.format("    %-35s %-20s", rset.getString("author"),rset.getString("sum"));
                                System.out.println(ss);
                            } 

                            System.out.println(); 
                            rset = stmt.executeQuery (
                                "select name, sum (amount_sold) from report, c_order, book_into_basket, book where report.date = c_order.date and book_into_basket.order_id = c_order.order_id and book_into_basket.ISBN = book.ISBN and report.date = " + "'"+userChoice+"'"+"group by name"
                            ); 
                             s = String.format("    %-35s %-20s", "name", "Amount Sold");
                            System.out.println(s);
                            while (rset.next()) {
                                String ss = String.format("    %-35s %-20s", rset.getString("name"),rset.getString("sum"));
                                System.out.println(ss);
                            }

                            System.out.println(); 
                            rset = stmt.executeQuery (
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
                }
                else if (userChoice.equals("4")){
                    System.out.println("Login out...");
                    logged_in = false;
                }
            }
            } catch (Exception sqle) {
            System.out.println("Exception : " + sqle);
        }
    }
}
