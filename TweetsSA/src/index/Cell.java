package index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
//import java.util.Queue;
import java.util.Set;
import java.util.Comparator;
import java.util.HashMap;
//import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import util.Point;
//import util.RQueryResult;
import util.Results;
import util.Batch;
import util.Keyword;
import util.MBR;
import util.Tweet;



public class Cell {

	public static int maxLevel = 10; 
	public static int maxCapacity = 2000;
	public int curCapacity ;

	public MBR mbr ;
	public int level ;
	public Cell leftUpCell, leftDownCell, rightUpCell, rightDownCell;
	public Cell parent;


	public HashMap<String, ArrayList<Tweet>> tweetList ;

	//public ArrayList<Tweet> tweetList ;

	public Cell(double minX, double maxX, double minY, double maxY, int level, Cell parent) {

		this.curCapacity = 0 ;
		this.mbr = new MBR(minX, maxX, minY, maxY) ;
		this.level = level ;
		this.leftUpCell = this.leftDownCell = this.rightUpCell = this.rightDownCell = null ;
		this.tweetList = new HashMap<String, ArrayList<Tweet>>();

		this.parent = parent;

	}

	public void setParameters(int level, int capacity) {
		this.maxLevel = level;
		this.maxCapacity = capacity;
	}

	public void split(){
		double subWidth = (double) (((this.mbr.rightDown.longitude) - (this.mbr.leftUp.longitude)) / 2.00);
		double subHeight = (double) (((this.mbr.leftUp.latitude)-(this.mbr.rightDown.latitude)) / 2.00);
		double x = this.mbr.leftUp.longitude;
		double y = this.mbr.rightDown.latitude;
		double maxX = this.mbr.rightDown.longitude;
		double maxY = this.mbr.leftUp.latitude;
		rightDownCell = new Cell(x + subWidth, maxX,y, y+subHeight,level + 1, this);
		leftDownCell = new Cell(x, x+subWidth, y, y+subHeight,level + 1, this);
		leftUpCell = new Cell(x, x+subWidth, y + subHeight,  maxY, level + 1, this);
		rightUpCell = new Cell(x + subWidth, maxX, y + subHeight, maxY,level + 1, this);
	}

	public Batch partition(Batch b) {
		Batch newB = new Batch();

		for(int i=0;i<b.TweetBuffer.size();i++) {
			Tweet t = b.TweetBuffer.get(i);
			if(this.mbr.isInside(t.location))
				newB.add(t);
		}
		for(int i=0;i<newB.TweetBuffer.size();i++) {
			b.TweetBuffer.remove(newB.TweetBuffer.get(i));
		}
		return newB;

	}

	public void insert(Batch b){

		if(this.isLeaf()){
			for(int i=0;i<b.TweetBuffer.size();i++) { 
				Tweet t = b.TweetBuffer.get(i);
				//this.tweetList.add(t);
				if(this.tweetList.containsKey(t.keyword)) {
					ArrayList<Tweet> temp = this.tweetList.get(t.keyword);
					temp.add(t);
				}
				else {
					ArrayList<Tweet> temp = new ArrayList<Tweet>();
					temp.add(t);
					this.tweetList.put(t.keyword, temp);
				}
				this.curCapacity ++;


				if (this.curCapacity > Cell.maxCapacity && this.level <= maxLevel) {

					this.split();

					for (String key : this.tweetList.keySet()) {

						ArrayList<Tweet> temp = this.tweetList.get(key);
						int j = 0;
						while (j < temp.size()) {
							this.insert(temp.get(j));
							j++;
						}}


					this.tweetList.clear();
					this.curCapacity = 0 ;


				}
			}
		}
		else {
			Batch b0=this.rightDownCell.partition(b);
			Batch b1=this.rightUpCell.partition(b);
			Batch b2=this.leftDownCell.partition(b);
			Batch b3=this.leftUpCell.partition(b);

			if(b0.TweetBuffer.size() != 0)
				this.rightDownCell.insert(b0);
			if(b1.TweetBuffer.size() != 0)
				this.rightUpCell.insert(b1);
			if(b2.TweetBuffer.size() != 0)
				this.leftDownCell.insert(b2);
			if(b3.TweetBuffer.size() != 0)
				this.leftUpCell.insert(b3);

		}
	}

	public void insert(Tweet t){

		if(this.isLeaf()){
			if(this.mbr.isInside(t.location)){

				if(this.tweetList.containsKey(t.keyword)) {
					ArrayList<Tweet> temp = this.tweetList.get(t.keyword);
					temp.add(t);
				}
				else {
					ArrayList<Tweet> temp = new ArrayList<Tweet>();
					temp.add(t);
					this.tweetList.put(t.keyword, temp);
				}
				this.curCapacity ++;
			}
		}
		else {
			if(this.rightDownCell.mbr.isInside(t.location))
				this.rightDownCell.insert(t);
			else if(this.leftDownCell.mbr.isInside(t.location))
				this.leftDownCell.insert(t);
			else if(this.leftUpCell.mbr.isInside(t.location))
				this.leftUpCell.insert(t);
			else if(this.rightUpCell.mbr.isInside(t.location))
				this.rightUpCell.insert(t);
			else {
				System.err.println("Something Strange Going On!");
			}

		}
	}

	public int countData() {
		if(!this.isLeaf()){
			return this.leftDownCell.countData()+
					this.leftUpCell.countData()+
					this.rightDownCell.countData()+
					this.rightUpCell.countData();
		}
		else {
			return this.curCapacity;	
		}

	}

	public boolean isLeaf() {
		if ( (this.leftUpCell == null) && (this.rightUpCell == null) && (this.leftDownCell == null) && (this.rightDownCell == null) )
			return true;
		return false;
	}

	//*********************************************

	public void getQueryCells(double x, double y, ArrayList<Cell> cells, MBR boxB ){
		if(boxB.overlaps(this.mbr)) {
			if(!this.isLeaf()){
				if(boxB.overlaps(this.leftDownCell.mbr)) 
					this.leftDownCell.getQueryCells(x,y,cells, boxB);
				if(boxB.overlaps(this.leftUpCell.mbr)) 
					this.leftUpCell.getQueryCells(x,y,cells, boxB);
				if(boxB.overlaps(this.rightUpCell.mbr)) 
					this.rightUpCell.getQueryCells(x,y,cells, boxB);
				if(boxB.overlaps(this.rightDownCell.mbr)) 
					this.rightDownCell.getQueryCells(x,y,cells, boxB);
			}
			else{
				cells.add(this);
			}
		}

	}
	
	
	
	//*****************************************

	public ArrayList<Tweet> rangeQuery(double x, double y, double range, int k, ArrayList<String> Qkeywords){

		ArrayList<Tweet> Result = new ArrayList<Tweet>();


		PriorityQueue<Results> PQueue = new PriorityQueue<Results>(new Comparator<Results>() {
			public int compare(Results r1, Results r2) {
				return Long.compare(r2.recentTime, r1.recentTime); 
			}
		});
		
		
		
		ArrayList<Cell> Qcells = new ArrayList<Cell>(); 
		MBR box = new MBR();
		box = box.getBoundingBox(x, y, range);
		getQueryCells(x,y,Qcells,box);

		for(Cell c : Qcells) {
			boolean CBQ = box.CCover(c.mbr);
			if(Qkeywords.size() <= c.tweetList.size()) {
				
				for (String temp : Qkeywords) {
					if(c.tweetList.containsKey(temp)) {

						ArrayList<Tweet> TList = c.tweetList.get(temp);
						PQueue.add(new Results(TList,TList.get(TList.size()-1).timestampL, TList.size()-1,CBQ)); 
					}}
			}

		}	


		while(!PQueue.isEmpty()) {
			Results temp = PQueue.poll();
			Tweet t = temp.list.get(temp.pointer);
			temp.pointer --;


			if(matchKW(Qkeywords,t)) {
				if(!temp.CoverdByQBox) {
					double Dis = box.distance(x, y, t.location.longitude,t.location.latitude, 'K');
					if(Dis < range) {

						Result.add(t);
					}
				}
				else {
					Result.add(t);
				}
			}
			if(temp.pointer >= 0)
				PQueue.add(new Results(temp.list,temp.list.get(temp.pointer).timestampL, temp.pointer, temp.CoverdByQBox));

			if(Result.size() >= k)
				break;
		}

		return Result;

	}

	public boolean matchKW(ArrayList<String> Qkeywords, Tweet t) {

		for(String kw : Qkeywords ) {
			String lowerText = t.keyword.toLowerCase();
			String lowerkw = kw.toLowerCase();
			if((lowerText.equals(lowerkw)))
				return true;
		}
		return false;
	}


	//************************************************

	public ArrayList<Keyword> rangeQueryWorkload(double x, double y, double range){

		ArrayList<Keyword> ListKW = new ArrayList<Keyword>();
		HashMap<String,Integer> keywords = new HashMap<String,Integer>();

		ArrayList<Cell> Qcells = new ArrayList<Cell>(); 
		MBR box = new MBR();
		box = box.getBoundingBox(x, y, range);
		getQueryCells(x,y,Qcells,box);

		for(Cell c : Qcells) {
			Set<String> keys = c.tweetList.keySet();
			for(String key: keys){
				if(keywords.containsKey(key)) {
					int occurrence = keywords.get(key) + c.tweetList.get(key).size();
					keywords.put(key, occurrence);
				}
				else {
					int occurrence = c.tweetList.get(key).size();
					keywords.put(key, occurrence);
				}
			}
		}
		
		Map<String, Integer>  sorted = sortByValue(keywords);
		
		
		for (Map.Entry<String, Integer> en : sorted.entrySet()) {
			Keyword kw = new Keyword(en.getKey(),en.getValue());
			ListKW.add(kw);
//            System.out.println("Key = " + en.getKey() +
//                          ", Value = " + en.getValue());
        }
		
//		for (Keyword kw : ListKW) {
//            System.out.println("Key = " + kw.kw +
//                          ", Value = " + kw.occurrence);
//        }
		
		return ListKW;

	}


	public void printThisRegion(double x,double y) {

		if(!this.isLeaf()){
			this.rightDownCell.printThisRegion(x,y);
			this.rightUpCell.printThisRegion(x,y);
			this.leftDownCell.printThisRegion(x,y);
			this.leftUpCell.printThisRegion(x,y);
		}
		else{
			if(this.mbr.isInside(new Point(x,y))) {
				System.err.println("Cell Capacity = "+this.curCapacity);
				this.mbr.toPrint();
				System.out.println(" ##################### ");
				for (String key : this.tweetList.keySet()) {
					ArrayList<Tweet> temp = this.tweetList.get(key);
					System.out.println(key);
					for(Tweet t: temp) {
						t.print();
					}
				}
			}
		}
	}

	public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
	{
		// Create a list from elements of HashMap
		List<Map.Entry<String, Integer> > list =
				new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2)
			{
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

}
