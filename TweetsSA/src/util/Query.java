package util;

import java.util.ArrayList;

public class Query {
	
	public double x;
	public double y;
	public ArrayList<String> keywords;
	
	public Query(double x, double y, ArrayList<String> keywords) {
		this.x = x;
		this.y = y;
		this.keywords = keywords;
	}
}
