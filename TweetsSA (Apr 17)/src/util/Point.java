package util;

public class Point {

    public double longitude ;
    public double latitude ;

    public Point(double longitude, double latitude) {
        this.longitude = longitude ;
        this.latitude = latitude ;
    }
    
    public void printPoints(){
		System.out.println("( "+longitude+","+latitude+" )");
	}

}