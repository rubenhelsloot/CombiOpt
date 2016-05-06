package case2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Tour {
	ArrayList<Edge> tour;

	Tour() {
		tour = new ArrayList<>();
	}
	
	Tour(Tour t) {
		tour = new ArrayList<>();
		for (Edge e : t.tour) {
			tour.add(new Edge(e));
		}
	}

	Tour cycle(Location l) {
		int max = tour.size();
		int i = 0;
		
		if (max == 0) {
			System.out.println("NULL!");
			return null;
		}

		while (tour.get(0).start.id != l.id && i < max) {
			Edge e = tour.get(0);
			tour.add(e);
			tour.remove(0);

			i++;
		}

		return this;
	}
	
	Tour removeDuplicates() {
		Iterator<Edge> itr = tour.iterator();
		
		while(itr.hasNext()) {
			Edge e = itr.next();
			if (e.end.id == e.start.id && (e.end.isDepot || e.end.r.id == e.start.r.id)){
				itr.remove();
			}
		}
		
		if (tour.size() < 2) {
			return null;
		}
		
		return this;
	}

	int size() {
		return tour.size();
	}

	int length() {
		int result = 0;
		for (Edge e : tour) {
			result += e.length;
		}
		return result;
	}

	// Maximum weight in vehicle during tour
	long weight() {
		Map<Integer, Long> weights = new HashMap<>();
		Map<Integer, Long> max = new HashMap<>();
		Map<Integer, Long> min = new HashMap<>();
		Map<Integer, Long> inVehicle = new HashMap<>();

		for (int i = 0; i < tour.size() - 1; i++) {
			Edge e = tour.get(i);
			if (!e.end.isDepot && weights.get(e.end.r.type) == null)
				weights.put(e.end.r.type, e.end.r.tool.size);

//			System.out.print("Type: " + e.end.r.type + " Amount: ");
//			if (e.end.r.delivered)
//				System.out.print("-");
//			System.out.print(e.end.r.amount + " In: " + inVehicle.getOrDefault(e.end.r.type, 0) + " Max: "
//					+ max.getOrDefault(e.end.r.type, 0) + " Min: " + min.getOrDefault(e.end.r.type, 0)
//					+ " " + e.end.r.delivered + "\n");
			
			if (e.end.isDepot) {
				inVehicle.clear();
			} else if (e.end.r.delivered) {
				long current = inVehicle.getOrDefault(e.end.r.type, 0L) + e.end.r.amount;
				inVehicle.put(e.end.r.type, current);
				max.put(e.end.r.type, Math.max(current, max.getOrDefault(e.end.r.type, 0L)));
			} else {
				long current = inVehicle.getOrDefault(e.end.r.type, 0L) - e.end.r.amount;
				inVehicle.put(e.end.r.type, current);
				min.put(e.end.r.type, Math.min(current, min.getOrDefault(e.end.r.type, 0L)));
			}
		}

		Set<Integer> weightSet = weights.keySet();
		int result = 0;
		for (int v : weightSet) {
			result += (max.getOrDefault(v, 0L) - min.getOrDefault(v, 0L)) * weights.get(v);
		}

		return result;
	}
	
	Tour merge(Tour other) {
		other.tour.remove(0);
		this.tour.remove(other.size() - 1);
		for (Edge e : other.tour) {
			this.tour.add(e);
		}
		return this;
	}
	
	Tour add(Tour other) {
		for (Edge e : other.tour) {
			tour.add(e);
		}
		return this;
	}

	void print(BufferedWriter bf) {
		String line = "Tour: ";
		for (Edge e : tour) {
			line += e.print();
		}
		
		try {
			bf.write(line);
			bf.newLine();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	String print() {
		String line = "Tour: ";
		for (Edge e : tour) {
			line += e.print();
		}
		
		return line;
	}

	boolean contains(Location l) {
		for (Edge e : tour) {
			if (l.id == e.start.id || l.id == e.end.id) {
				return true;
			}
		}
		return false;
	}
	
	boolean validate(Depot depot) {
		return (length() < depot.maxDist && weight() < depot.maxCap);
	}
}
