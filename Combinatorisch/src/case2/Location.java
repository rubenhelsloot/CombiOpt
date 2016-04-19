package case2;

public class Location {
	int x;
	int y;
	
	Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	int distance(Location l) {
		double dx = Math.pow(l.x - this.x, 2.0);
		double dy = Math.pow(l.y - this.y, 2.0);
		return (int) Math.floor(Math.sqrt(dx + dy));
	}
}
