package util;

import java.util.ArrayList;

public class Results implements Comparable<Results> {
	
	public ArrayList<Tweet> list;
	public long recentTime;
	public int pointer;
	public boolean CoverdByQBox;
	
	public Results(ArrayList<Tweet> list, long time, int pointer, boolean CBQ ) {
		this.list = list;
		this.recentTime = time;
		this.pointer = pointer;
		this.CoverdByQBox = CBQ;
	}
	
	@Override
    public int compareTo(Results other) {
		
		return Long.compare(other.recentTime, this.recentTime);

    }
}