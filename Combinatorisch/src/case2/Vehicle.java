package case2;

import java.util.ArrayList;

public class Vehicle {

	int maxCap;
	int capacity;
	int maxDist;
	int distTravelled;
	ArrayList<Tool> load;
	Request[] planning;
	Tour tour;

	Vehicle(int maxCap, int maxDist) {
		this.maxCap = maxCap;
		this.maxDist = maxDist;
		load = new ArrayList<Tool>(maxCap);
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

	void load(Location l) {
		for(int i = 0; i < r.amount; i++){
			if(load.add(r.t)){
				capacity += t.size;
				t.globalAvailable--;
			}
		}
	}

	void unload(Tool t) {
		if(load.remove(t)){
			capacity -= t.size;
			t.globalAvailable++;
		}
	}

	void activity(Request r){
		if(r.delivered){
			load(r.);
		}

	}



	void plan(Request[] planning) {
		this.planning = planning;
	}
}
