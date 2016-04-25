package case2;

import java.util.ArrayList;

public class Location implements Comparable {
	int id;
	int x;
	int y;
	double angle; //with depot
	boolean visited;
	
	Location(int id, int x, int y, int dx, int dy) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = Math.atan2(y - dy, x - dx);
		
		init();
	}
	
	Location(int id, int x, int y, Location d) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = Math.atan2(y - d.y, x - d.x);
		
		init();
	}
	
	Location(int id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = 0;
		init();
	}
	
	void init() {
		visited = false;
	}
	
	void visit() {
		visited = true;
	}
	
	int distance(Location l) {
		double dx = Math.pow(l.x - this.x, 2.0);
		double dy = Math.pow(l.y - this.y, 2.0);
		return (int) Math.floor(Math.sqrt(dx + dy));
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
    
    void print() {
		System.out.println("(" + x + ", " + y + ") " + angle);
	}
}
