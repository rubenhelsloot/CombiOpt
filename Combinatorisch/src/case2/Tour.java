package case2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Tour extends ArrayList<Edge> {

	Tour() {
		super();
	}
	
	Tour(Tour t) {
		super();
		for (Edge e : t) {
			add(new Edge(e));
		}
	}

	Tour cycle(Location l) {
		int max = size();
		int i = 0;
		
		if (max == 0) {
			System.out.println("NULL!");
			return null;
		}

		while (get(0).start.id != l.id && i < max) {
			Edge e = get(0);
			add(e);
			remove(0);

			i++;
		}

		return this;
	}
	
	Tour removeDuplicates() {
		if (this == null)
			return null;
		Iterator<Edge> itr = this.iterator();
		
		while(itr.hasNext()) {
			Edge e = itr.next();
			if (e.end.id == e.start.id && (e.end.isDepot || e.end.r.id == e.start.r.id)){
				itr.remove();
			}
		}
		
		return this;
	}
	
	long costs (long distanceCost, long vehicleDayCost) {
		long costs = this.length() * distanceCost + vehicleDayCost;
		return costs;
	}

	int length() {
		int result = 0;
		for (Edge e : this) {
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

		for (int i = 0; i < size() - 1; i++) {
			Edge e = get(i);
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
	
	Tour simpleMerge(Tour other) {
		other.remove(0);
		this.remove(other.size() - 1);
		for (Edge e : other) {
			this.add(e);
		}
		return this;
	}
	
	Tour merge(Tour other, int[] array, int offset) {
		remove(array[offset + 2]);
		other.remove(array[offset + 3]);
		other.removeDepot();
		
		add(offset, new Edge (get(array[offset + 2] - 1).end, other.get(array[offset + 3] - 2).end));
		addAll(offset, other);
		add(offset, new Edge (get(array[offset] - 1).end, other.get(array[offset + 2] - 2).end));
		
		return this;
	}
	
	int find(int id) {
		int i = 0;
		while (i < size()) {
			if (get(i).end.id == id)
				break;
			i++;
		}
		if (i == size()) {
			System.out.println("Not found");
			return -1;
		}
		return i;
	}
	
	Tour removeFind(int id) {
		remove(find(id));
		return this;
	}
	
	Edge retrieve(int id) {
		return get(find(id));
	}
	
	Tour removeDepot() {
		remove(0);
		add(new Edge(get(size() - 1).end,get(0).start));
		return this;
	}
	
	int[] closestPair(Tour other) {
		other.remove(0);
		int[] result = new int[5];
		int dist = Integer.MAX_VALUE;
		int l1 = size(), l2 = other.size();
		for (int i = 0; i < l1 - 1; i++) {
			for (int j = 0; j < l2 - 1; j++) {
				boolean reverse = false;
				int tmp = get(i).end.distance(other.get(j).end) + get(i + 1).end.distance(other.get(j + 1).end);
				int tmp2 = get(i + 1).end.distance(other.get(j).end) + get(i).end.distance(other.get(j + 1).end);
				if (tmp <= tmp2) {
					tmp -= other.get(j).length - get(i).length;
				} else {
					tmp = tmp2 - other.get(j).length - get(i).length;
					reverse = true;
				}
				
				if (tmp < dist) {
					dist = tmp;
					result = reverse ? new int[]{i + 1, j, i, j + 1, dist} : new int[]{i, j, i + 1, j + 1, dist};
				}
			}
		}
		return result;
	}
	
	Tour cheapestInsertion(ArrayList<Location> locations, int[][] distances, Depot depot) {
		int i = size();

		int total = locations.size() + size();
		while (i < total) {
			Edge e = get(0);
			int replace = 0;
			Location l = locations.get(0);
			double dist = Double.MAX_VALUE;

			for (int j = 0; j < locations.size(); j++) {
				if (!locations.get(j).visited) {
					for (int k = 0; k < i; k++) {
						if (insertionGain(distances, get(k), locations.get(j).id) < dist) {
							dist = insertionGain(distances, get(k), locations.get(j).id);
							e = get(k);
							l = locations.get(j);
							replace = k;
						}
					}
				}
			}

			set(replace, new Edge(e.start, l));
			add(replace + 1, new Edge(l, e.end));
			l.visit();
			i++;
		}

		removeDuplicates().cycle(depot.location);
		if (this != null) {
			makeValid(depot);
		}

		return this;
	}
	
	int insertionGain(int[][] distances, Edge e, int id) {
		return distances[e.start.id][id] + distances[e.end.id][id] - e.length;
	}
	
//	Tour addTour(Tour other) {
//		for (Edge e : other) {
//			add(e);
//		}
//		return this;
//	}

	void print(BufferedWriter bf) {
		String line = "Tour: ";
		for (Edge e : this) {
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
		for (Edge e : this) {
			line += e.print();
		}
		
		return line;
	}

	boolean contains(Location l) {
		for (Edge e : this) {
			if (l.id == e.start.id || l.id == e.end.id) {
				return true;
			}
		}
		return false;
	}
	
	boolean validate(Depot depot) {
		return (length() < depot.maxDist && weight() < depot.maxCap);
	}
	
	Tour makeValid(Depot depot) {
		if (this == null)
			return null;
		Location prevend = depot.location;
		
		int i = 0;
		while (i < size()) {
			Edge e = get(i);
			if (e.start != prevend) {
				add(i, new Edge(prevend, e.start));
			} else {
				i++;
				prevend = e.end;
			}
		}
		
		Iterator<Edge> itr = this.iterator();
		while(itr.hasNext()) {
			Edge e = itr.next();
			if (e.length == 0 && e.start.isDepot) {
				itr.remove();
			}
		}
		
		return this;
	}
}
