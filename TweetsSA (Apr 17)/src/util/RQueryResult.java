package util;

public class RQueryResult {
	
	public Tweet tweet;
	public long dateMilli;
	
	public RQueryResult(Tweet t) {
		this.tweet = t;
		this.dateMilli = t.timestampL;
	}
}