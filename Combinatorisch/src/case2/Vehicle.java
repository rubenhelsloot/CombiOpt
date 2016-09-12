package case2;

import java.io.IOException;
import java.util.ArrayList;

public class Vehicle {

	int id;
	int maxCap;
	int weight;
	int maxDist;
	int distTravelled;
	ArrayList<Tool> load;
	Request[] planning;
	Tour t;
	Depot depot;

	Vehicle(int id, int maxCap, int maxDist, Depot depot) {
		this.id = id;
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
		// System.out.println("Loading depot: " + l.r.amount + " out of " +
		// depot.toolsByType(l.r.type) + " (t" + l.r.type
		// + ") for R" + l.r.id + " @" + l.id);

		int loaded = 0;
		int i = 0;
		while (loaded < l.r.amount) {
			if (l.r.type == depot.toolstack.get(i).type) {
				weight += depot.toolstack.get(i).size;
				load.add(depot.toolstack.remove(i));
				loaded++;
				// System.out.println(
				// "Depotloading: " + loaded + " out of " + l.r.amount + " from
				// " + id + " (" + t.size() + ")");
			} else {
				i++;
			}
		}

	}

	void load(Location l) {
		// try {
		// depot.bf.write("Loading: @" + l.id + " R" + l.r.id + " " + l.r.amount
		// + " from " + id + " (" + t.size() + ")");
		// depot.bf.newLine();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		// System.out.println(
		// "Loading: @" + l.id + " R" + l.r.id + " " + l.r.amount + " from " +
		// id + " (" + t.size() + ")");
		for (int i = 0; i < l.r.amount; i++) {
			load.add(l.r.stack[i]);
			weight += l.r.stack[i].size;
		}

		l.r.clearStack();
	}

	void unload(Location l) {
		if (l.isDepot) {
			// try {
			// depot.bf.write("Unloading: @D " + load.size());
			// depot.bf.newLine();
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// }
			// System.out.println("Unloading: @D " + load.size());
			while (load.size() > 0) {
				Tool t = load.remove(0);
				depot.toolstack.add(t);
				weight -= t.size;
			}
		} else {
			// try {
			// depot.bf.write("Unloading: @" + l.id + " R" + l.r.id + " " +
			// l.r.amount);
			// depot.bf.newLine();
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// }
			// System.out
			// .println("Unloading: @" + l.id + " R" + l.r.id + " " + l.r.amount
			// + " of " + toolsByType(l.r.type));
			int unloaded = 0;
			int i = 0;
			do {
				if (load.get(i) == null) {
					i++;
					continue;
				}
				if (l.r.type == load.get(i).type) {
					l.r.stack[unloaded] = load.remove(i);
					weight += l.r.stack[unloaded].size;
					unloaded++;
				} else {
					i++;
				}
			} while (unloaded < l.r.amount);
		}
	}

	void addTour(Tour t) {
		this.t = t.makeValid(depot);
		// System.out.println("VALID TOUR: " + t.validate(depot));
		for (int i = 0; i < t.size(); i++) {
			if (t.get(i).start.isDepot) {
				int j = i;

				while (!t.get(j).end.isDepot) {
					if (!t.get(j).end.r.delivered) {
						loadFromDepot(t.get(j).end);
					}

					j++;
				}
			}

			if (t.get(i).end.isDepot) {
				unload(t.get(i).end);
			} else if (t.get(i).end.r.delivered) {
				load(t.get(i).end);
			} else {
				unload(t.get(i).end);
			}
		}
		// try {
		// depot.bf.write("Weight: " + weight + ", length: " + t.length() +
		// "\n");
		// depot.bf.newLine();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
	}
}
