package util;

public class MBR {

	public Point leftUp ;
	public Point rightDown ;


	public MBR() {

	}

	public MBR(double minX, double maxX, double minY, double maxY) {
		if(minX > maxX || minY > maxY)
			System.out.println("The MBR is invalid.");
		else {
			this.leftUp = new Point(minX, maxY) ;
			this.rightDown = new Point(maxX, minY) ;
		}
	}

	public boolean CCover(MBR other){
		return this.isInside(other.leftUp) && this.isInside(other.rightDown);
	}
	
	public boolean isInside(Point p){
		if(p.longitude >= leftUp.longitude && p.longitude <= rightDown.longitude
				&& p.latitude <= leftUp.latitude && p.latitude >= rightDown.latitude)
			return true;
		return false;		
	}

	public boolean isInside(double x, double y){
		if(x >= leftUp.longitude && x <= rightDown.longitude
				&& y <= leftUp.latitude && y >= rightDown.latitude)
			return true;
		return false;		
	}

	public MBR shiftToPositive(MBR box) {
		return new MBR(box.leftUp.longitude+180,box.rightDown.longitude+180,box.rightDown.latitude+90,box.leftUp.latitude+90);
	}
	public double getOverlapArea(MBR other) {
		return Math.max(0, Math.min(this.rightDown.longitude, other.rightDown.longitude) 
				- Math.max(this.leftUp.longitude, other.leftUp.longitude)) 
				* Math.max(0, Math.min(this.leftUp.latitude, other.leftUp.latitude) - 
						Math.max(this.rightDown.latitude, other.rightDown.latitude));
	}

	public double getOverlapAreaV2(MBR other) {
		MBR temp1 = shiftToPositive(this);
		MBR temp2 = shiftToPositive(other);
		return Math.max(0, Math.min(temp1.rightDown.longitude,temp2.rightDown.longitude) 
				- Math.max(temp1.leftUp.longitude, temp2.leftUp.longitude)) 
				* Math.max(0, Math.min(temp1.leftUp.latitude, temp2.leftUp.latitude) - 
						Math.max(temp1.rightDown.latitude, temp2.rightDown.latitude));
	}

	public Point getCentroid() {
		Double Long = this.leftUp.longitude + (this.rightDown.longitude - this.leftUp.longitude)/2;
		Double Lat = this.rightDown.latitude + (this.leftUp.latitude-this.rightDown.latitude)/2;
		Point p = new Point(Long,Lat);
		return p;
	}

	public boolean overlaps(MBR other){
		//First bounding box, top left corner, bottom right corner
		double ATLx = this.leftUp.longitude; 
		double ATLy = this.leftUp.latitude;
		double ABRx = this.rightDown.longitude;
		double ABRy = this.rightDown.latitude;

		//Second bounding box, top left corner, bottom right corner
		double BTLx = other.leftUp.longitude;
		double BTLy = other.leftUp.latitude;
		double BBRx = other.rightDown.longitude;
		double BBRy = other.rightDown.latitude;

		double rabx = Math.abs(ATLx + ABRx - BTLx - BBRx);
		double raby = Math.abs(ATLy + ABRy - BTLy - BBRy);

		//rAx + rBx
		double raxPrbx = ABRx - ATLx + BBRx - BTLx;

		//rAy + rBy
		double rayPrby = ATLy - ABRy + BTLy - BBRy;

		if(rabx <= raxPrbx && raby <= rayPrby)
		{
			return true;
		}
		return false;
	}


	public double getArea(){
		return Math.abs((rightDown.longitude - leftUp.longitude)) * Math.abs((leftUp.latitude - rightDown.latitude));
	}

	public double getAreaInK(){
		return distance(leftUp.longitude,leftUp.latitude,rightDown.longitude,leftUp.latitude,'K') *
				distance(rightDown.longitude,leftUp.latitude,rightDown.longitude,rightDown.latitude,'K');
	}

	public double distance(double x1, double y1, double x2, double y2, char unit) {
		//public double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = x1 - x2;
		double dist = Math.sin(deg2rad(y1)) * Math.sin(deg2rad(y2)) + Math.cos(deg2rad(y1)) * Math.cos(deg2rad(y2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	public MBR getBoundingBox(double x, double y, double distance){

		double R = 6371; // earth radius in km 26.447521
		double radius = distance;//0.300; // km


		double minX = (x - Math.toDegrees(radius / R / Math.cos(Math.toRadians(y))));
		double maxX = (x + Math.toDegrees(radius / R / Math.cos(Math.toRadians(y))));
		double maxY = (y + Math.toDegrees(radius / R));
		double minY = (y - Math.toDegrees(radius / R));
		if(maxX > 180)
			maxX = 180;
		if(maxY > 90)
			maxY = 90;
		if(minX < -180)
			minX = -180;
		if(minY < -90)
			minY = -90;

		MBR box = new MBR(minX,maxX,minY,maxY);
		return box;
	}

	public double costTovisit(double x, double y, MBR box){
		double minx = box.leftUp.longitude;
		double maxx = box.rightDown.longitude; 
		double miny = box.rightDown.latitude;
		double maxy = box.leftUp.latitude;
		if(box.isInside(x,y))
			return 0;
		else{
			if(x <= minx && y <= miny)
				return distance(x, y, minx,miny, 'K');
			else if(x >= maxx && y <=miny)
				return distance(x, y, maxx,miny, 'K');
			else if(x >= maxx && y >= maxy)
				return distance(x, y, maxx,maxy, 'K');
			else if(x <= minx && y >= maxy)
				return distance(x, y, minx,maxy, 'K');
			else if(x <= maxx && x >= minx && y <= miny)
				return distance(x, y, x,miny, 'K');
			else if(x <= maxx && x >= minx && y >= maxy)
				return distance(x, y, x,maxy, 'K');
			else if(y <= maxy && y >= miny && x <= minx)
				return distance(x, y, minx,y, 'K');
			else if(y <= maxy && y >= miny && x >= maxx)
				return distance(x, y, maxx,y, 'K');
		}
		return 0;
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts decimal degrees to radians             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	public double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts radians to decimal degrees             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	public double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	public void toPrintBox() {
		System.out.println(this.leftUp.longitude+","+this.rightDown.longitude+","+this.leftUp.latitude+","+this.rightDown.latitude);
	}

	public void toPrint() {
		System.out.println("Left-Up "+this.leftUp.longitude+","+this.leftUp.latitude);
		System.out.println("Right-Down "+this.rightDown.longitude+","+this.rightDown.latitude);
	}
}