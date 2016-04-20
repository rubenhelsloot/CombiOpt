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

	void scheduleMusts(int carpool, int maxDistance, int[][] distances, int depotId) {
		ArrayList<Request> pickups = new ArrayList<>();
		ArrayList<Request> deliveries = new ArrayList<>();
		for (Request r : musts) {
			if (r.delivered) {
				pickups.add(r);
			} else {
				deliveries.add(r);
			}
		}

		Stack convexHull = new Stack();
		convexHull.push(depot.location);
		
		cheapestInsertion(convexHull, musts, distances, depotId);
	}

	void recursiveSchedule(ArrayList<Request> pickups, ArrayList<Request> deliveries) {

		// if(pickups.size() > 0) {
		// Request r = pickups.get(0);
		// deliveries = sortByLocation(deliveries, r.location);
		//
		// }
	}

	Edge[] cheapestInsertion(Stack convexHull, ArrayList<Request> locations, int[][] distances, int depotId) {
		Edge[] edges = new Edge[locations.size()];
		Node current = convexHull.header;
		int i = 0;
		while (current.next != null) {
			edges[i] = new Edge(current.data, current.next.data);
			current.data.visit();
			current.next.data.visit();
			current = current.next;
			i++;
		}

		while (i < locations.size()) {
			Edge e = edges[0];
			int replace = 0;
			int a = 0;
			while (locations.get(a).location.visited) {
				a++;
			}
			Location l = locations.get(a).location;
			double dist = insertionGain(distances, e, depotId);
			for (int j = 0; j < locations.size(); j++) {
				if (!locations.get(j).location.visited) {
					for (int k = 0; k < i; k++) {
						if (insertionGain(distances, edges[k], locations.get(j).id) < dist) {
							dist = insertionGain(distances, edges[k], locations.get(j).id);
							e = edges[k];
							l = locations.get(j).location;
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
		return distances[e.start.id][id] + distances[e.end.id][id] - distances[e.start.id][e.end.id];
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
}