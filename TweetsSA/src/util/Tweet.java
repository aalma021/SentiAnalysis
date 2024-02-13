package util;

public class Tweet {

	
	public String keyword;
	public String text;
	public Point location;
	public long ID;
	public double NLPScore;
		public long timestampL;
	
	public Tweet(long ID,Point location,double NLPScore,String keyword,String text,long clock){
		this.keyword = keyword;
		this.text=text;
		this.location = location;
		this.ID = ID;
		this.NLPScore=NLPScore;
		this.timestampL=clock;
		
	}
	
	public void print() {
		System.out.println(ID+" --- "+location.longitude+" --- "+location.latitude+" --- "+NLPScore+" --- "+keyword+" --- "+text+" --- "+timestampL);
	}
}