package case2;

import java.util.ArrayList;

public class Vehicle {

	int maxCap;
	int weight;
	int maxDist;
	int distTravelled;
	ArrayList<Tool> load;
	Request[] planning;
	Tour t;
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
		for (Tool t : load) {
			if (t.type == type)
				result++;
		}
		return result;
	}

	void loadFromDepot(Location l) {
		int type = l.r.type;

		for (int i = 0; i < l.r.amount; i++) {
			for (int j = 0; j < depot.toolstack.size(); j++) {					
				if(type == depot.toolstack.get(j).type) {
					load.add(depot.toolstack.get(j));
					weight += depot.toolstack.get(j).size;
					depot.toolstack.remove(j);

					j = depot.toolstack.size() - 1;
				}
			}
		}
	}

	void load(Location l) {
		for (int i = 0; i < l.r.amount; i++) {
			load.add(l.r.stack[i]);
			weight += l.r.stack[i].size;
		}

		l.r.clearStack();
	}

	void unload(Location l) {
		if (l.isDepot) {
			for(int i = 0; i < load.size(); i++) {
				depot.toolstack.add(load.get(i));
				weight -= load.get(i).size;
				load.remove(i);
			}
		} else {
			int type = l.r.type;
			
			for (int i = 0; i < l.r.amount; i++) {
				for (int j = 0; j < load.size(); j++) {
					if (load.get(j).type == type) {
						l.r.stack[i] = load.get(j);
						load.remove(j);
						weight -= l.r.stack[i].size;
						
						j = load.size() - 1;
					}
				}
			}
		}
	}

	void addTour(Tour t) {
		this.t = t;

		int i = 0;

		System.out.println("Tour size: " + t.tour.size());
		
		while(t.tour.get(i) != t.tour.get(t.tour.size() - 1)) {
			if(t.tour.get(i).start.id == depot.location.id) {
				int j = 0;

				while(t.tour.get(j).end.id != depot.location.id) {
					if(!t.tour.get(j).end.r.delivered) {
						loadFromDepot(t.tour.get(j).end);
					}

					j++;
				}
				
				System.out.println("Weight: " + weight);
			} 
						
			if(t.tour.get(i).end.id == depot.location.id) {
				unload(t.tour.get(i).end);
			} else {
				if(t.tour.get(i).end.r.delivered) {
					load(t.tour.get(i).end);
				} else {
					unload(t.tour.get(i).end);
				}
			}
			
			System.out.println("Weight: " + weight);
			
			i++;
		}
		
		int length = 0;
		
		for(int j = 0; j < t.tour.size(); j++) {
			length += t.tour.get(j).length;
		}
		
		System.out.println("Length: " + length);
	}

	void plan(Request[] planning) {
		this.planning = planning;
	}
}
