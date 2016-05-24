package com.db.web.application;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class AccessCounter {

	private static Map<String, Integer> objects = new HashMap<String, Integer>();
	public static int value = 0;
	public ReentrantLock lock;
	 
	 AccessCounter(){
		 lock= new ReentrantLock();
	 }
	
	public static void print(){
		System.out.println("\n");
		System.out.println("--------Access Counter Details---------");
		System.out.println();
		System.out.format("%15s%15s","File Name" ," Access Count");
		System.out.println("\n");
		for (Map.Entry<String,Integer> entry : objects.entrySet()) {
      	  String key = entry.getKey();
      	  int value = entry.getValue();
      
      	System.out.format("%15s%10s",key +" : " ,value);
      	System.out.println();
      	}
		System.out.println();
	}

	
	public void increment(String name) {
		lock.lock();
		try{
		Set<String> keys = objects.keySet();

		if (!keys.contains(name)) {
			objects.put(name, 1);
		} else {
			int fileCount = objects.get(name);
			objects.put(name, fileCount + 1);
			System.out.print("######  File Name : " + name );
		}
		}finally{
			lock.unlock();
		}
	}

	public int getCount(String name) {
		lock.lock();
		try{
		int count = objects.get(name);
		System.out.print("Access Count : " +count);
		System.out.println("");
		return count;
		}
		finally{
			lock.unlock();
			System.out.println("");
		}		
	}
}	
