package com.db.web.application;

public class DBTest {

	public static void dbSetup(String str) {
				
		JDBCTest test = new JDBCTest();
		boolean val = test.getDBName();
		
		if(!val){
			JDBCTest.createDB();
			JDBCTest.createDBTable();
			JDBCTest.insertData();
		}else{
			System.out.println("the schema is present");
		}
		
		JDBCTest.createFile(str);
	}

}
