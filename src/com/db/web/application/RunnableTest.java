package com.db.web.application;
import java.net.Socket;


public class RunnableTest implements Runnable
{
	private Socket s;
	
	public RunnableTest(Socket client)
	{
		this.s = client;
	}
	
	public void run()
	{
		for(int i=0; i<10; i++)
		{
			System.out.println(s);
		}
	}
}
