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
		if (depot.toolsByType(l.r.type) < l.r.amount)
			return;

		int loaded = 0;
		int i = 0;
		while (i < depot.toolstack.size() && (loaded < l.r.amount)) {
			if (l.r.type == depot.toolstack.get(i).type) {
				load.add(depot.toolstack.remove(i));
				weight += depot.toolstack.get(i).size;
				loaded++;
				// System.out.println("Loading: " + loaded + " out of " +
				// l.r.amount);
			} else {
				i++;
			}
		}
	}

	void load(Location l) {
//		System.out.println("Loading: @" + l.id + " R" + l.r.id + " " + l.r.amount);
		for (int i = 0; i < l.r.amount; i++) {
			load.add(l.r.stack[i]);
			weight += l.r.stack[i].size;
		}

		l.r.clearStack();
	}

	void unload(Location l) {
//		System.out.println("Unloading: @" + l.id + " R" + l.r.id + " " + l.r.amount);
		if (l.isDepot) {
			while (load.size() > 0) {
				Tool t = load.remove(0);
				depot.toolstack.add(t);
				weight -= t.size;
			}
		} else {
			int unloaded = 0;
			int i = 0;
			while (unloaded < l.r.amount) {
				if (l.r.type == load.get(i).type) {
					l.r.stack[unloaded] = load.remove(i);
					weight += l.r.stack[unloaded].size;
					unloaded++;
					// System.out.println("Unloaded: " + unloaded + " out of " +
					// l.r.amount);
				} else {
					i++;
				}
			}
		}
	}

	void addTour(Tour t) {
		this.t = t;

		for (int i = 0; i < t.tour.size() - 1; i++) {
			if (t.tour.get(i).start.isDepot) {
				int j = i;

				while (!t.tour.get(j).end.isDepot) {
					if (!t.tour.get(j).end.r.delivered) {
						loadFromDepot(t.tour.get(j).end);
					}

					j++;
				}
			}

			if (t.tour.get(i).end.id == depot.location.id) {
				unload(t.tour.get(i).end);
			} else if (t.tour.get(i).end.r.delivered) {
				load(t.tour.get(i).end);
			} else {
				unload(t.tour.get(i).end);
			}
		}
		System.out.print("Weight: " + weight + ", length: " + t.length() + "\n");
	}
}
