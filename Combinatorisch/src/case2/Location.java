package case2;

import java.io.BufferedWriter;
import java.io.IOException;

public class Location implements Comparable, Cloneable {
	int id;
	int x;
	int y;
	double angle; // with depot
	boolean visited;
	boolean isDepot;
	Request r;
	int cluster;

	Location(Request r, int[][] coordinates, int dx, int dy) {
		id = r.locationId;
		x = coordinates[id][1];
		y = coordinates[id][2];
		angle = Math.atan2(y - dy, x - dx);
		isDepot = (x == dy && y == dy && angle == 0);
		this.r = r;
		visited = false;
		cluster = 0;
	}

	Location(Request r, int[][] coordinates, Location d) {
		id = r.locationId;
		x = coordinates[id][1];
		y = coordinates[id][2];
		angle = Math.atan2(y - d.y, x - d.x);
		isDepot = (x == d.y && y == d.y && angle == 0);
		this.r = r;
		visited = false;
		cluster = 0;
	}

	Location(int id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = 0;
		isDepot = true;
		visited = false;
		cluster = 0;
	}
	
	Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

	void endOfDay() {
		reset();
		r.endOfDay();
	}
	
	void reset() {
		visited = false;
		cluster = 0;
	}

	void visit() {
		visited = true;
	}
	
	void setCluster(int i) {
		cluster = i;
	}

	int distance(Location l) {
		double dx = Math.pow(l.x - this.x, 2.0);
		double dy = Math.pow(l.y - this.y, 2.0);
		return (int) Math.floor(Math.sqrt(dx + dy));
	}
	
	boolean classifyMusts(int day) {
		return ((r.delivered && r.remaining == 0) || (!r.delivered && r.last == day));
	}
	
	boolean classifyMays(int day) {
		return (!r.delivered && day >= r.first);
	}

	void print(BufferedWriter bf) {
		String line = "(" + x + ", " + y + ") " + angle;
		try {
			bf.write(line);
			bf.newLine();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public int compareTo(Object other) {
		Location test = (Location) other;
		if (angle < test.angle) {
			return -1;
		}
		if (angle > test.angle) {
			return 1;
		}
		if (angle == test.angle && angle != Math.PI) {
			if (y < test.y) {
				return -1;
			}
			if (y > test.y) {
				return 1;
			}
			if (y == test.y) {
				return 0;
			}
		}
		if (angle == test.angle && angle == Math.PI) {
			if (x > test.x) {
				return -1;
			}
			if (x < test.x) {
				return 1;
			}
			if (x == test.x) {
				return 0;
			}
		}
		return 0;
	}
}