# Database-webapp

Requirements:
1) JAVA_HOME (1.7.x version or higher) 
2) MySQL work bench


					Project details and execution steps
					
Implementation details
When we execute the project, the JDBC class will check for the existence of “dbproject” 
database in local MySQL server. 
	If the db is present, then it will check for the tables and data. 
	If the db is not present, "dbproject" db will be created and the corresponding tables will 
be created. The data will be read from the files under TestFiles/*.html and the data will 
be stored in the files table with the file name

When we run ant build for the Database-webapp and browse localhost:8888 on the web browser, the db will 
be checked for the requested file (default is index.html)and the data will be fetched and stored in the empty files and that will 
be displayed on the web browser.

The following are implemented in this project
Persistent Connection
HTTP post form
Thread Pool
Access Counter
Caching (LFU, LRU)
JDBC class for Storing files and data in MYSQL database 
Code in Object oriented fashion

Execution plan:
Before executing the project, please change the following properties for MySQL db in JDBCTest.java, line 
number 136 to 138.
Username: root
Password: 
Port: 3303