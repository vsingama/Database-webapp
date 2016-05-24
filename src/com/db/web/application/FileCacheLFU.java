package com.db.web.application;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileCacheLFU implements FileCache {

	private static int initialCapacity = 3;
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	class CacheEntry {
		private String data;
		private int frequency;

		// default constructor
		private CacheEntry() {
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public int getFrequency() {
			return frequency;
		}

		public void setFrequency(int frequency) {
			this.frequency = frequency;
		}

	}

	private static HashMap<String, CacheEntry> cacheMap = new HashMap<String, CacheEntry>();

	@Override
	public String fetch(String targetFile) {
		// TODO Auto-generated method stub
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(targetFile.contains(".jpg") || targetFile.contains(".png")|| targetFile.contains(".ico"))
		{
			
		}
		else{
			System.out.println();
			System.out.println("---------------File Cache LFU-------------------");
			System.out.println();
		System.out.println("Reading ..... " + targetFile);
		
		
		if (cacheMap.containsKey(targetFile)) {
			lock.readLock().lock();
			System.out.println("(R) lock acquired");
			CacheEntry temp = new CacheEntry();
			System.out.println(targetFile+  " - File is Already Present in the Hashmap.");
			System.out.println("Hence Incrementing the counter Value...");
			System.out.println("Done..!!");
			int val=cacheMap.get(targetFile).getFrequency();
			String str=cacheMap.get(targetFile).getData();
			temp.setData(str);
			temp.setFrequency(val+1);
			cacheMap.put(targetFile, temp);
		//System.out.println("Data in the file : " + cacheMap.get(targetFile).getData() + ", Counter Value is :" +(val+1));
			//System.out.println();
			
			lock.readLock().unlock();
			System.out.println("(R) lock Released");
			return temp.getData();
		}
		else {
			lock.writeLock().lock();
			System.out.println("(W) lock Acquired");
			cacheFile(targetFile);
			lock.writeLock().unlock();
			System.out.println("(W) lock Released");
			
		}
		}
		return null;
	}

	private String cacheFile(String targetFile) {
		try{
			File file = new File(targetFile);
		    StringBuffer contents = new StringBuffer();
		    BufferedReader reader = null;

		    reader = new BufferedReader(new FileReader(file));
		    String text = null;

		    // repeat until all lines is read
		    while ((text = reader.readLine()) != null) {
		      contents.append(text).append(System.getProperty("line.separator"));
		    }
		    reader.close();                               
		if (!isFull()) {
				CacheEntry temp = new CacheEntry();
				temp.setData(contents.toString());
				temp.setFrequency(1);

				cacheMap.put(targetFile, temp);
				System.out.println("FileCache is not full..");
				System.out.println("inserting " + targetFile +" .."+" Done !!");
			//	System.out.println("Data in the file is  :- " + temp.getData());
			} else {
				String entryKeyToBeRemoved = getLFUKey();
				cacheMap.remove(entryKeyToBeRemoved);
				CacheEntry temp = new CacheEntry();
				temp.setData(contents.toString());
				temp.setFrequency(1);

				cacheMap.put(targetFile, temp);
				System.out.println("inserting " + targetFile +" .." +"Done !!");
				System.out.println("Data is inserted in the HashMap");
			//	System.out.println("Data in the file is  :- " + temp.getData());
			}
		}catch(IOException e){e.printStackTrace();}
		return null;
	}

	public String getLFUKey() {
		String key = null;
		int minFreq = Integer.MAX_VALUE;

		for (Entry<String, CacheEntry> entry : cacheMap.entrySet()) {
			if (minFreq > entry.getValue().frequency) {
				key = entry.getKey();
				minFreq = entry.getValue().frequency;
			}
		}
		System.out.println("Oops..!! HashMap reached its maximum Size..!!");
		System.out.println("LFU Key to be removed is :" + key);
		System.out.println("Deleting....! \nDeleted..!!");
		return key;
	}

	public static boolean isFull() {
		if (cacheMap.size() == initialCapacity)
			return true;

		return false;
	}
}