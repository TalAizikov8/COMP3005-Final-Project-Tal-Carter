# COMP3005-Final-Project-Tal-Carter
### This repository is for a bookstore. Made for COMP3005 final project. By Tal Aizikov and Carter Grad

#### Instructions for compile and run
1. Download the github repository and unzip it
2. Create new database in pgadmin4 and name it COMP3005FinalProject
3. Run bookstoreDDL.sql file inside the SQL folder (in pgadmin4)
4. Run bookstoreRelations.sql file inside the SQL folder (in pgadmin4)
5. Run triggerFunction.sql file inside the SQL folder (in pgadmin4)
6. Compile bookstore.java in bookstore folder(we used command line **instructions below**)
7. To run the book store use java in command line, **instructions below**


##### To compile bookstore.java
1. Change line 7 with your pgadmin4 password 
2. Make sure line 48 and line 355 have the same name at the data base (ex Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/COMP3005FinalProject", userid, passwd);     <---- the name of the data base must be COMP3005FinalProject
3. open command line in the folder where the bookstore.java is saved
4. type javac bookstore.java

##### To run bookstore.java
1. open command line in the folder where the bookstore.java is saved
2. run java -cp "path to postgresql-42.3.1;path to the folder which contains bookstore.class file" bookstore
###### Please note you might have to download a different version of postgresql JDBC driver (most likely not on newer systems)

##### We used a lot of queries inside the java, all of their functionality is commented in the java

