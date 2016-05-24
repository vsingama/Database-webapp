package com.db.web.application;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;


public class TinyHttpd4Server{
    Socket client;

    String line = null;
    String httpVersion = null;
    boolean connectionKeepAlive=false;
    @SuppressWarnings("rawtypes")
	private ArrayList files = new ArrayList();
    @SuppressWarnings("rawtypes")
	ArrayList lineArray = new ArrayList();
    @SuppressWarnings("rawtypes")
	ArrayList tokens = new ArrayList();
    ArrayList requestHeaderLines;
    StringTokenizer st = null;
    private ServerSocket serverSocket;
    ReentrantLock lock = new ReentrantLock();
    private static final int PORT = 8888;
    
    public void init(){
		try{
			try{
				StaticThreadPool pool = new StaticThreadPool(2, true);
				
				serverSocket = new ServerSocket(PORT);
				System.out.println("Socket created.");
			
				while(true){	
					System.out.println( "Listening to a connection on the local port " +
										serverSocket.getLocalPort() + "..." );
					Socket client = serverSocket.accept();
					pool.execute(new RunnableTest(client));
					System.out.println( "\nA connection established with the remote port " + 
										client.getPort() + " at " +
										client.getInetAddress().toString() );
					//executeCommand( client );
					System.out.println(" printing client : " +client.toString());
				}
			}
			finally{
				serverSocket.close();
			}
		}
		catch(IOException exception){
			exception.printStackTrace();
		}
	}
    
    @SuppressWarnings("unchecked")
	private void executeCommand( Socket client ){
        try {
            try {
                String token = null;                
                client.setSoTimeout(30000);
                BufferedReader in = new BufferedReader( new InputStreamReader( client.getInputStream() ) );  
                PrintStream out = new PrintStream( client.getOutputStream() );    
                
                System.out.println( "I/O setup done" );             
                String line = in.readLine();    //Get first line of request to check the request command GET or HEAD            
                System.out.println("Printing the input : " + line);
                String reqCommand = null;
                StringTokenizer tokenLine = null;
                if(line.length() > 0){
                 tokenLine = new StringTokenizer(line);
                 reqCommand = tokenLine.nextToken();       
                }
                if (reqCommand.equals("GET") || reqCommand.equals("HEAD")|| reqCommand.equals("POST")) //only GET or HEAD requests are accepted, else gives 501 error
                {
                  String fname = tokenLine.nextToken();

                  if(fname.startsWith("/") || fname.contains("index.html") || fname.equals("/"))
                  {
                	 
                	  String filename = null;
  					 if(!fname.equalsIgnoreCase("/")){
  						 StringTokenizer tokenizedLine1 = new StringTokenizer(fname,"/");	
  				 
                 	   filename = tokenizedLine1.nextToken();
  					 }else{
  						 filename="index.html";
  					 }

                	  StringTokenizer tokenizedLine = new StringTokenizer(line);
                         
                          while(tokenizedLine.hasMoreTokens())
                          {
                              token = tokenizedLine.nextToken();
                              if(token.endsWith(".html")||token.endsWith(".jpg")||token.endsWith(".png")) //checking for file types.
                                  files.add(token);
                              if(token.startsWith("HTTP")) //checking version of the HTTP connection.
                                  if(token.contains("/1.0"))
                                      httpVersion = "HTTP/1.0";
                                  else if(token.contains("/1.1"))
                                      httpVersion = "HTTP/1.1";                               
                              if(token.equals("Connection:")) //parsing request to get connection type
                                  if(tokenizedLine.nextToken().equals("keep-alive")) //checking for connection type
                                      connectionKeepAlive = true;
                          } 
                       //   line = in.readLine();
                   
                           
        
                      File file = new File(filename);

      			      if( file.exists() == false)
                      {
      			    	out.println("<h1>Error Code: 404 \n     File Not Found....</h1> \n");
      			    	out.println("<h1>Try the URL  localhost:8888\\index.html </h1>");
      			    	out.println("<h1>localhost:8888\\Test1.html </h1>");
      			    	out.println("<h1>localhost:8888\\Test2.html</h1>");
      			    	out.println("<h1>localhost:8888\\upload.html</h1>");
                      }	
                      else
                      {
                    	  lock.lock();
                    	  DBTest.dbSetup(filename);
                    	  lock.unlock();
                    	  AccessCounter ac = new AccessCounter(); 
                          System.out.println("#### Calling Access Counter : " + filename);
                          ac.increment(filename);
                          System.out.println();
                          FileCacheLFU lfu = new FileCacheLFU();
                  		  lfu.fetch(filename);
                  		  FileCacheLRU lru=new FileCacheLRU();
                  		  lru.fetch(filename);
                  		  
                          AccessCounter.print();
                          if(filename.equals("index.html")|| filename.equals("Test1.html") ||filename.equals("upload.html")||filename.equals("Test2.html") )
                          sendFile(out, file);                      
                          else
                          {
                        	  out.println("<h1>Error Code: 404 \n     File Not Found.... \n    Try the URL  localhost:8888\\index.html \n localhost:8888\\Test1.html \n localhost:8888\\Test2.html</h1>");
                          }
                      }
                 }
                 else
                 {
                      out.println("Error Code: 404 \n     File Not Found.... \n    Try the URL localhost:8888  or localhost:8888\\index.html ");
                 }
               }
               else
               {
                   out.println("Error code: 501 \n Not Implemented Error....");  
               }
               

               if(client.isClosed())
                   System.out.println("client is closed before finally");

            }finally {
                if(httpVersion.equals("HTTP/1.0") && connectionKeepAlive == false)
                {
                    client.close();
                    System.out.println( "HTTP/1.0 and Keep-alive = false: Connection is closed." );
                }
                else if(httpVersion.equals("HTTP/1.0") && connectionKeepAlive == true)
                {
                    client.setKeepAlive(true);
                    System.out.println("client keep alive: "+client.getKeepAlive());
                    client.setSoTimeout(5000);
                    System.out.println("So_timeout set: "+client.getSoTimeout());
                }
                else if(httpVersion.equals("HTTP/1.1"))
                {
                    client.setKeepAlive(true);
                    System.out.println("client keep alive: "+client.getKeepAlive());
                    client.setSoTimeout(5000);
                    System.out.println("So_timeout set: "+client.getSoTimeout());
                }
            }
        }
        catch(SocketTimeoutException e)
        {
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Socket timeout, Connection closed.");
        }
        catch(Exception exception) {
            exception.printStackTrace();
        }
    } 

    private void sendFile(PrintStream out, File file){
    	 try{
             Date date = new Date();
             DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
             out.println(httpVersion+" OK");
             out.println("Content-Type: text/html");
             out.println("Date: "+dateFormat.format(date));
             int len = (int) file.length();
             out.println("Content-Length: " + len);
             out.println("");  

             DataInputStream fin = new DataInputStream(new FileInputStream(file));
             byte buf[] = new byte[len];

             fin.readFully(buf);
             out.write(buf, 0, len);
             out.println("/nFiles in this request are: ");
             Iterator it = files.iterator();
             while(it.hasNext())
             {
                 out.println(it.next().toString());
             }
             out.flush();
             fin.close();
         }catch(IOException exception){
             exception.printStackTrace();
         }         
     }

    
    public static void main(String[] args) {
    	TinyHttpd4Server server = new TinyHttpd4Server();
		server.init();
	}
    public class RunnableTest implements Runnable
	{
		private Socket socket;
		
		public RunnableTest(Socket socket2)
		{
			this.socket = socket2;
		}
		
		public void run()
		{
			executeCommand( socket );
		}
	}
}