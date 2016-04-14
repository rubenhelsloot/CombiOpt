package case2;

import java.util.ArrayList;

public class Vehicle {

	int maxCap;
	int capacity;
	int maxDist;
	int distTravelled;
	ArrayList<Tool> load;
	Request[] planning;
	
	Vehicle(int maxCap, int maxDist) {
		this.maxCap = maxCap;
		this.maxDist = maxDist;
		load = new ArrayList<>();
		init();
	}
	
	void init() {
		capacity = 0;
		distTravelled = 0;
	}
	
	int toolsByType(int type) {
		int result = 0;
		for(Tool t : load) {
			if(t.type == type) result ++;
		}
		return result;
	}
	
	void load(Tool t) {
		
	}
	
	void unload(Tool t) {
		
	}
	
	void plan(Request[] planning) {
		this.planning = planning;
	}
}
