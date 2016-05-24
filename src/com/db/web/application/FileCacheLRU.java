package com.db.web.application;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileCacheLRU implements FileCache {

	private static int initialCapacity = 3;
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	class CacheEntry {
		private String data;
		private long time;

		// default constructor
		private CacheEntry() {
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long l) {
			this.time = l;
		}


	}

	private static HashMap<String, CacheEntry> cacheMap = new HashMap<String, CacheEntry>();

	//List<CacheEntry> l = new ArrayList<CacheEntry>(cacheMap.values());

	@Override
	public String fetch(String targetFile) {
		// TODO Auto-generated method stub
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(targetFile.contains(".jpg") || targetFile.contains(".png") || targetFile.contains(".ico"))
		{
		
		}
		else{
			System.out.println();
			System.out.println("---------------File Cache LRU-------------------");
			System.out.println();
			System.out.println("Reading ..... " + targetFile);
		if (cacheMap.containsKey(targetFile)) {
			lock.readLock().lock();
			System.out.println("(R) lock acquired");
			CacheEntry temp = new CacheEntry();
			System.out.println();
			System.out.println(targetFile+  " - File is Already Present in the Hashmap.");
			System.out.println("Hence updating the latest timestamp...");
			System.out.println("Done..!!");
			String str=cacheMap.get(targetFile).getData();
			temp.setData(str);
			temp.setTime(System.currentTimeMillis());
			cacheMap.put(targetFile, temp);
		//.out.println("Data in the file : " + cacheMap.get(targetFile).getData());
			
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
	  //  System.out.println(contents.toString());

	  
				if (!isFull()) {
					CacheEntry temp = new CacheEntry();
					temp.setData(contents.toString());
					temp.setTime(System.currentTimeMillis());
					cacheMap.put(targetFile, temp);
				//	System.out.println();
					System.out.println("FileCache is not full..");
					System.out.println("inserting " + targetFile +" .."+" Done !!");
					System.out.println("Data is inserted in the HashMap");
					//System.out.println("Data in the file is  :- " + temp.getData());
					//System.out.println();
				} else {
					String entryKeyToBeRemoved = getLRUKey();
					cacheMap.remove(entryKeyToBeRemoved);
					CacheEntry temp = new CacheEntry();
					temp.setData(targetFile);
					temp.setTime(System.currentTimeMillis());

					cacheMap.put(targetFile, temp);
					System.out.println("inserting " + targetFile +" .." +"Done !!");
				//	System.out.println("Data in the file is  :- " + temp.getData());
					//System.out.println();

			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

	public String getLRUKey() {
		String key = null;
		long minFreq = Long.MAX_VALUE;

		for (Entry<String, CacheEntry> entry : cacheMap.entrySet()) {
			if (minFreq > entry.getValue().time) {
				key = entry.getKey();
				minFreq = entry.getValue().time;
			}
		}
		System.out.println("Oops..!! HashMap reached its maximum Size..!!");
		System.out.println("LRU Key to be removed is :" + key);
		System.out.println("Deleting....! \nDeleted..!!");
		return key;
	}

	public static boolean isFull() {
		if (cacheMap.size() == initialCapacity)
			return true;

		return false;
	}
	

}