package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import index.Cell;
import util.Batch;
import util.Point;
import util.Query;
import util.Tweet;

public class Indexing {

	public static Cell MyIndex;
	public static int num = 0;
	public static int batchSize = 1000;
	public static long elapsedTimeIndex = 0;
	public static long elapsedTimeQuery = 0;
	public static long MyClock = 0;
	public static ArrayList<Query> query = new ArrayList<Query>();
	public static int range;
	public static int rate;
	public static int k;
	public static int kwNum;
	public static int DataSet;
	public static String DFile;
	public static File QFile;

	public static void main(String args[]) throws Exception {

		if(args[0]!=null)
			DFile=args[0];
	//	System.out.println(DFile);
		
		if(args[1]!=null)
			rate=Integer.parseInt(args[1]);
				
		if(args[2]!=null)
			batchSize=Integer.parseInt(args[2]);

		if(args[3]!=null)
			k=Integer.parseInt(args[3]);
		
		if(args[4]!=null)
			range=Integer.parseInt(args[4]);
		
		if(args[5]!=null)
			kwNum=Integer.parseInt(args[5]);		
		
		if(args[6]!=null)
			DataSet=Integer.parseInt(args[6]);	
		
		if(args[7]!=null)
			QFile=new File(args[7]);
		//System.out.println(QFile);
		
		//System.out.println("System is starting ...");
		
		MyIndex = new Cell(-180.0, 180.0, -90.0, 90.0, 0, null);
		readQueries();
		
		long startTimeG = System.nanoTime();
		begin();
		elapsedTimeIndex = System.nanoTime() - startTimeG;
		
		long startTimeQ = System.nanoTime();
		startQuerying();
		elapsedTimeQuery = System.nanoTime() - startTimeQ;
		
		System.out.println("K= "+k+"  Range= "+range+"  Numbers of Keywords= "+kwNum+"  DataSet Size= "+DataSet);
		
		
		System.out.print("No. of Tweets= "+num+"	");
		System.out.print("No. of Indexed Tweets= "+MyIndex.countData()+"	");
		System.out.print("Memory before gc= "+getMemoryUse() / (double)(1024 * 1024 * 1024)+"	");
		System.gc();
		System.out.print("Memory after gc= "+getMemoryUse() / (double)(1024 * 1024 * 1024)+"	");
		System.out.print("TimeIndex= "+(double) (elapsedTimeIndex / 1000000000.0)+"	");
		System.out.println();
		System.out.print("TimeQuery= "+(double) (elapsedTimeQuery / query.size()*1000000000.0)+"	");
		System.out.println();
		// for testing
		//MyIndex.printThisRegion(-84,35);
		
		// Test RQ
		//ArrayList<String> keywords = new ArrayList<String>();
		//keywords.add("year");
		//keywords.add("happy");
		
		
		/*ArrayList<Tweet> temp = MyIndex.rangeQuery(-84,35,100, 10,keywords);
		for(Tweet t: temp) {
			t.print();
		}*/
		
	}

	public static void begin() throws IOException {
		//System.out.println("The data is being indexing ...");
		MyIndex = new Cell(-180.0, 180.0, -90.0, 90.0, 0, null);
		startIndexing();
		//System.out.println("Finishing Indexing ...");
	}


	public static void startIndexing() {
		ArrayList < File > files = new ArrayList < File > ();
		listf(DFile, files);//"/home/aalma021/Desktop/CleanData"

		for (int i = 0; i < files.size(); i++) {
			try {
				//System.out.println(files.get(i).getAbsolutePath());
				read(files.get(i).getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	//********************
	
		public static void startQuerying() throws IOException{
			//
					for(Query q: query) {
						MyClock ++;
						ArrayList<String> key = new ArrayList<String>(q.keywords.subList(0, kwNum));
						//System.out.println(key);
						ArrayList<Tweet> qResults = MyIndex.rangeQuery(q.x,q.y,range,k,key);
					//	System.out.println(qResults.size());
						/*for(Tweet t:qResults){
							t.print();
						}*/
						
					}
					
					
				}
		
		
		//*******************
		
		public static void readQueries() throws IOException {
			
			
			File file = QFile;
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = br.readLine()) != null) {
					ArrayList<String> keywords=new ArrayList<String>();
					try{
						String splitted[] = line.split(",");
						for(int i=2;i<splitted.length;i++)
						 { 
							keywords.add(splitted[i]);
						 }
						Query q = new Query(Double.parseDouble(splitted[0]),Double.parseDouble(splitted[1]),keywords);
						query.add(q);
					} catch (Exception e) {
						e.printStackTrace();

					}
				}
			}
		}
		
		//******************************************************
	

	public static void read(String directory) throws IOException {

		Batch b = new Batch();
		File file = new File(directory);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				try{

					Tweet t = getTweetObject(line);
					num++;
					b.add(t);

					if(num % rate == 0) {
						MyClock ++;
					}

					if(b.TweetBuffer.size() >= batchSize) {
						MyIndex.insert(b);
						b = new Batch();
					}
					
					if(num>DataSet)
					{
						break;
					}

				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}

			if(b.TweetBuffer.size() > 0) {

				MyIndex.insert(b);
			}

			br.close();
		}


	}



	public static Tweet getTweetObject(String line){
		String splitted[] = line.split(",");
		String text = "";
		//		//boolean foundHashTag = false;
		for(int i=4;i<splitted.length;i++) {
		
				text += splitted[i] +" ";
					//foundHashTag = true;
		
				}
	/*	int numTry = 0;
		while(numTry < (splitted.length-5)) {
			numTry++;
			int rand = getRandNum(5, splitted.length);
			if(rand == 0)
				text = "";
			else {
				text = splitted[rand];
				//if(text.length() > 3)
					//break;
			}
		}*/
		Point p = new Point(Double.parseDouble(splitted[1]),Double.parseDouble(splitted[2]));
		Tweet t = new Tweet(Long.parseLong(splitted[0]),p,Double.parseDouble(splitted[3]),splitted[4],text,MyClock);
		return t;
	}


	public static void listf(String directoryName, ArrayList < File > files) {

		File directory = new File(directoryName);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file: fList) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files);
			}
		}

	}

	public static long getMemoryUse() {

		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		return (totalMemory - freeMemory);

	}

	public static int getRandNum(int low, int high){
		if(high == low) {	
			return 0;
		}
		Random r = new Random();
		return r.nextInt(high-low) + low;

	}


}