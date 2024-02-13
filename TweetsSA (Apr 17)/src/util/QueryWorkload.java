package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import test.Indexing;


public class QueryWorkload {
	
	static int numOfQuery = 1000;
	public static Indexing MyIndex = new Indexing();
	static ArrayList<Point> pointsList = new ArrayList<Point>();
	static ArrayList<Query> queryList = new ArrayList<Query>();
	
	public static void main(String args[]) throws IOException {
		MyIndex.begin();
		readPoints();
		genQueryWorkload();
		saveQuery();
//		MyIndex.MyIndex.rangeQueryWorkload(-84.84016,32.35465,10);
	}
	
	public static void genQueryWorkload() {
		Set<Integer> set = new HashSet<Integer>();
		while(queryList.size() < 1000) {
			int randNum = getRandNum(0,1000000);
			if(set.contains(randNum))
				continue;
			else
				set.add(randNum);
			Point p = pointsList.get(randNum);
			ArrayList<Keyword> ListKW = MyIndex.MyIndex.rangeQueryWorkload(p.longitude,p.latitude,10);
			if(ListKW.size() == 0)
				continue;
			ArrayList<String> FinalKWs = new ArrayList<String>();
			int start = 0;
			int end = 10;
			while(FinalKWs.size() < 6) {
				int size = ListKW.size();
				if(end > size)
					end = size;
				if(start > size)
					start = size;
				int rand = getRandNum(start, end);
				String qkw = ListKW.get(rand).kw;
				FinalKWs.add(qkw);
				start += 10;
				end += 50;
			}
			Query q = new Query(p.longitude,p.latitude,FinalKWs);
			queryList.add(q);
		}
		System.out.println(queryList.size());
	}
	
	public static void saveQuery() throws IOException {
		File dir = new File("C:\\Users\\Aisha\\Desktop\\Final Project\\Code");
		if(!dir.exists())
			dir.mkdir();

		boolean append = false;
		FileWriter fileWriter = new FileWriter(dir.getAbsolutePath()+"\\query.txt", append);
		BufferedWriter writer = new BufferedWriter(fileWriter);

		for(Query q:queryList) { 
			writer.write(String.valueOf(q.x)+",");
			writer.write(String.valueOf(q.y)+",");
			for(String k:q.keywords)
				writer.write(k+",");
			writer.write("\n");
		}
		writer.close();
	}
	
	public static void readPoints() throws IOException {
		InputStream gzip = new FileInputStream("C:\\Users\\Aisha\\Desktop\\Final Project\\Code\\lat-longs-1000K.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
		String line;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			String[] splited = line.split("\\s+");
			Point p = new Point(Double.parseDouble(splited[1]),Double.parseDouble(splited[0]));
			pointsList.add(p);
//			p.printPoints();
		}
//		System.out.print(pointsList.get(10));
		br.close();
	}
	
	public static int getRandNum(int low, int high){
		if(high == low) {	
			return 0;
		}
		Random r = new Random();
		return r.nextInt(high-low) + low;

	}
	
	
}
