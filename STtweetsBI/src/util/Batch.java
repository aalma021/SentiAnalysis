package util;

import java.util.ArrayList;

public class Batch {
	
	public double minX, minY;
	public double maxX, maxY;
	public ArrayList<Tweet> TweetBuffer;
	
	public Batch()
	{
		minX = minY = 1000;
		maxX = maxY = -1000;
		TweetBuffer = new ArrayList<Tweet>();
	}
	
	public void add(Tweet t) {
		
		TweetBuffer.add(t);
		
		minX = Math.min(minX, t.location.longitude);
		maxX = Math.max(maxX, t.location.longitude);

		minY = Math.min(minY, t.location.latitude);
		maxY = Math.max(maxY, t.location.latitude);
		
	}
	
	public MBR getMBR() {
		MBR box = new MBR(minX,maxX,minY,maxY);
		return box;
	}

	
}