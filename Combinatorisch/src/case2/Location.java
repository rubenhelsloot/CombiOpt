package case2;

import java.util.ArrayList;

public class Location implements Comparable {
	int id;
	int x;
	int y;
	double angle; //with depot
	boolean visited;
	ArrayList<Request> requests;
	ArrayList<Request> must;
	ArrayList<Request> may;
	
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
		requests = new ArrayList<>();
	}
	
	void addRequest(Request r) {
		requests.add(r);
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
    
    boolean classifyRequests(int day) {
    	must = new ArrayList<>();
    	may = new ArrayList<>();
    	boolean hasMusts = false;
    	
    	for (Request r : requests) {
    		if ((r.delivered && r.remaining == 0) || (!r.delivered && r.last == day)) {
				hasMusts = true;
				must.add(r);
    		} else if (!r.delivered && day >= r.first) {
    			may.add(r);
    		}
    	}
    	return hasMusts;
    }
    
    void print() {
		System.out.println("(" + x + ", " + y + ") " + angle);
	}
}
