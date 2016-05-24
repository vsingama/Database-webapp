package com.db.web.application;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTest{
	
	public boolean getDBName() {
		Connection conn = JDBCTest.getURLConnection();
		
		boolean dbFound = false;

		try {
			
			String sql = "show databases";	
			Statement stmt=conn.createStatement();  
			  
			ResultSet rs=stmt.executeQuery(sql);
			System.out.println(rs);
			int i=1;
			while(rs.next())  {
				if(rs.getString(i).equals("dbproject")){
					dbFound = true;
				}
			}
			conn.close();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return dbFound; 
	}
	
	public static void createDB(){
		Connection conn = JDBCTest.getURLConnection();
		try {
		
			String sql = "create database dbproject";	
			Statement stmt=conn.createStatement();  
			  
			int rs=stmt.executeUpdate(sql);
			System.out.println(rs);
			
			conn.close();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}

	public static void createDBTable(){
			Connection conn = JDBCTest.getURLConnection();

		try {
			
			String sql = "create table dbproject.files(fileName VARCHAR(20), fileContent VARCHAR(2000));";	
			Statement stmt=conn.createStatement();  
			  
			int rs=stmt.executeUpdate(sql);
			System.out.println(rs);
			
			conn.close();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
		public static void insertData(){
			Connection conn = JDBCTest.getURLConnection();
			String filePath1[] ={"index.html","upload.html","Test1.html","Test2.html"};
			String filePath[] ={"TestFiles/index.html","TestFiles/upload.html","TestFiles/Test1.html","TestFiles/Test2.html"};

			try {		
				System.out.println("Inserting file data into the Database....");
				
				for (int i=0;i<4;i++){
				String sql = "INSERT INTO dbproject.files (fileName, fileContent) values (?, ?)";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, filePath1[i]);
				InputStream inputStream = new FileInputStream(new File(filePath[i]));
				statement.setBlob(2, inputStream);

				int row = statement.executeUpdate();
				if (row > 0) {
					System.out.println("The file "+ filePath[i].toString() +" data is inserted in the Database.");
				}
				}
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		public static void createFile(String filePath) {
			
			Connection conn = JDBCTest.getURLConnection();
			try{
				String sql = "select fileName,fileContent from dbproject.files where filename ='"+filePath+"'" ;

				Statement st = conn.createStatement();

				ResultSet rs = st.executeQuery(sql);
				File f=new File(filePath);
			      @SuppressWarnings("resource")
				FileOutputStream fop = new FileOutputStream(f);
			      while (rs.next())
			      {
			    	String content = rs.getString(2);

			        byte[] contentInBytes = content.getBytes();
			         fop.write(contentInBytes);
			      }

			      st.close();
			    }
			    catch (Exception e)
			    {
			      System.err.println("Got an exception! ");
			      System.err.println(e.getMessage());
			      e.printStackTrace();
			    }
			  }
		
		public static Connection getURLConnection(){
			String url = "jdbc:mysql://localhost:3306/";
			String user = "root";
			String password = "";
			Connection conn = null;
			
			try {
				 conn = DriverManager.getConnection(url, user, password);
				System.out.println(conn);
				}catch (Exception e)
		    {
				      System.err.println("Got an exception! ");
				      System.err.println(e.getMessage());
				      e.printStackTrace();
				    }
			
			return conn;
			
		}
	}
