package util;

import java.util.ArrayList;

public class Results {

	public ArrayList<Tweet> list;
	public long recentTime;
	public int pointer;
	public boolean CoverdByQBox;
	
	public Results(ArrayList<Tweet> list, long time, int pointer, boolean CBQ ) {
		this.list = list;
		recentTime = time;
		this.pointer = pointer;
		this.CoverdByQBox = CBQ;
	}
}