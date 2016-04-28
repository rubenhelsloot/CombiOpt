package case2;

import java.util.ArrayList;

public class Vehicle {

	int maxCap;
	int weight;
	int maxDist;
	int distTravelled;
	ArrayList<Tool> load;
	Request[] planning;
	Tour tour;
	Depot depot;

	Vehicle(int maxCap, int maxDist, Depot depot) {
		this.maxCap = maxCap;
		this.maxDist = maxDist;
		this.depot = depot;
		load = new ArrayList<Tool>();
		init();
	}

	void init() {
		weight = 0;
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
		if(l.isDepot) {

		} else {
			for(int i = 0; i < l.r.amount; i++) {
				for(int j = 0; j < l.r.stack.length; j++) {
					load.add(l.r.stack[j]);
					weight += l.r.stack[j].size;
				}

				l.r.clearStack();
			}
		}
	}

	void unload(Location l) {
		if(l.isDepot) {

		} else {
			int type = l.r.type;

			for(int i = 0; i < l.r.amount; i++) {
				for(int j = 0; j < load.size(); j++) {
					if(load.get(j).type == type) {
						l.r.stack[i] = load.get(j);
						load.remove(j);
						weight -= l.r.stack[i].size;
					}
				}
			}
		}
	}

	void activity(Request r){
		if(r.delivered){

		}

	}



	void plan(Request[] planning) {
		this.planning = planning;
	}
}
