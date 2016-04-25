package case2;

import java.util.ArrayList;

public class Day {

	public ArrayList<Request> musts;
	public ArrayList<Request> mays;
	public int dayId;
	private Depot depot;

	Day(int dayId, Depot depot) {
		this.depot = depot;
		this.dayId = dayId;
		musts = new ArrayList<>();
		mays = new ArrayList<>();
	}

	void init(ArrayList<Request> requests) {
		classifyTasks(requests);
	}

	void classifyTasks(ArrayList<Request> requests) {
		// Need pickup or is last day of time window
		for (Request r : requests) {
			if ((r.delivered && r.remaining == 0) || (!r.delivered && r.last == dayId)) {
				musts.add(r);
			} else if (!r.delivered && dayId >= r.first) {
				mays.add(r);
			}
		}
	}

	int getInitialToolSpace(int[][] tools) {
		int result = 0;
		for (Request r : musts) {
			if (r.delivered) {
				result -= r.stack[0].size * r.amount;
			} else {
				result += new Tool(r.type, tools).size * r.amount;
			}
		}
		return result;
	}

	void scheduleMusts(int carpool, int maxDistance, int[][] distances) {
		ArrayList<Request> pickups = new ArrayList<>();
		ArrayList<Request> deliveries = new ArrayList<>();
		for (Request r : musts) {
			if (r.delivered) {
				pickups.add(r);
			} else {
				deliveries.add(r);
			}
		}

		if (musts.size() > 0) {
			ArrayList<Location> mustsL = new ArrayList<>();
			for (Request r : musts) {
				mustsL.add(r.location);
			}

			// convexHull.push(depot.location);
			// convexHull.push(musts.get(0).location);
			// musts.remove(0);
			// convexHull.push(depot.location);
			print(mustsL);
			Stack convexHull = convexHullFinder(mustsL);
			cheapestInsertion(convexHull, musts, distances);
		}
	}

	void recursiveSchedule(ArrayList<Request> pickups, ArrayList<Request> deliveries) {

		// if(pickups.size() > 0) {
		// Request r = pickups.get(0);
		// deliveries = sortByLocation(deliveries, r.location);
		//
		// }
	}

	Edge[] cheapestInsertion(Stack convexHull, ArrayList<Request> requests, int[][] distances) {
		Edge[] edges = new Edge[convexHull.size()];
		Node current = convexHull.header;
		int i = 0;
		while (current.next != null) {
			edges[i] = new Edge(current.data, current.next.data);
			current.data.visit();
			current.next.data.visit();
			current = current.next;
			i++;
		}

		while (i < convexHull.size()) {
			Edge e = edges[0];
			int replace = 0;
			int a = 0;
			while (requests.get(a).location.visited) {
				a++;
			}
			Location l = requests.get(a).location;
			double dist = insertionGain(distances, e, depot.location.id);
			for (int j = a + 1; j < requests.size(); j++) {
				if (!requests.get(j).location.visited) {
					for (int k = 0; k < i; k++) {
						if (insertionGain(distances, edges[k], requests.get(j).id) < dist) {
							dist = insertionGain(distances, edges[k], requests.get(j).id);
							e = edges[k];
							l = requests.get(j).location;
							replace = k;
						}
					}
				}
			}
			edges[replace] = new Edge(e.start, l);
			edges[i] = new Edge(e.end, l);
			l.visited = true;
			i++;
		}
		int total = 0;
		for (Edge e : edges) {
			total += e.length;
		}
		System.out.println("Cheapest insertion length: " + total);
		return edges;
	}

	int insertionGain(int[][] distances, Edge e, int id) {
		return distances[e.start.id][id] + distances[e.end.id][id] - e.length;
	}

	public Stack convexHullFinder(ArrayList<Location> locations) {
		Stack stack = new Stack();
		sortByAngle(locations);
		stack.push(locations.get(locations.size() - 1));
		stack.push(locations.get(0));
		int i = 1;
		while (i < locations.size()) {
			System.out.println(i);
			if (!isRight(locations.get(i), stack.header.data, stack.header.next.data)) {
				stack.push(locations.get(i));
				i++;
			} else {
				stack.pop();
			}
		}
		return stack;
	}

	ArrayList<Request> sortByLocation(ArrayList<Request> requests, Location l) {
		if (requests.size() < 2)
			return requests;

		for (int i = 0; i < requests.size() - 1; i++) {
			for (int j = 0; j < requests.size() - i - 1; j++) {
				if (requests.get(j).location.distance(l) > requests.get(j + 1).location.distance(l)) {
					Request r = requests.get(j);
					requests.set(j, requests.get(j + 1));
					requests.set(j + 1, r);
				}
			}
		}

		return requests;
	}

	public ArrayList<Location> sortByAngle(ArrayList<Location> locations) {		
		if (locations.size() < 2)
			return locations;

		for (int i = 0; i < locations.size() - 1; i++) {
			for (int j = 0; j < locations.size() - i - 1; j++) {
				if (locations.get(j).angle > locations.get(j + 1).angle) {
					Location l = locations.get(j);
					locations.set(j, locations.get(j + 1));
					locations.set(j + 1, l);
				}
			}
		}
		print(locations);
		return locations;
	}

	void print(ArrayList<Location> locations) {
		for (int i = 0; i < locations.size(); i++) {
			locations.get(i).print();
		}
	}

	boolean isRight(Location P, Location Ptop, Location Ptop1) {
		return ((Ptop.x - Ptop1.x) * (P.y - Ptop1.y)
				- (Ptop.y - Ptop1.y) * (P.x - Ptop1.x) <= 0);
	}

}