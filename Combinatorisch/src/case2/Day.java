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

			// convexHull.push(depot.location);
			// convexHull.push(musts.get(0).location);
			// musts.remove(0);
			// convexHull.push(depot.location);
			Stack convexHull = convexHullFinder(musts);
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
		Edge[] edges = new Edge[requests.size()];
		Node current = convexHull.header;
		int i = 0;
		
		for (int x = 0; x < requests.size(); x++) {
			System.out.println(requests.get(x).id);
		}
		
		convexHull.print();
		
		while (current.next != null) {
			edges[i] = new Edge(current.data, current.next.data);
			current.data.location.visit();
			current.next.data.location.visit();
			current = current.next;
			i++;
		}

		while (i < requests.size()) {
			Edge e = edges[0];
			int replace = 0;
			int a = 0;
			while (requests.get(a).location.visited) {
				a++;
				if (a == requests.size()) {
					System.out.println("Hull contained all nodes");
					for (int z = 0; z < requests.size(); z++) {
						System.out.println(requests.get(z).first + " - " + requests.get(z).last + " " + requests.get(z).id);
					}
					return edges;
				}
			}
			Request r = requests.get(a);
			double dist = insertionGain(distances, e, depot.location.id);
			for (int j = a + 1; j < requests.size(); j++) {
				if (!requests.get(j).location.visited) {
					for (int k = 0; k < i; k++) {
						if (insertionGain(distances, edges[k], requests.get(j).id) < dist) {
							dist = insertionGain(distances, edges[k], requests.get(j).id);
							e = edges[k];
							r = requests.get(j);
							replace = k;
						}
					}
				}
			}
			edges[replace] = new Edge(e.start, r);
			edges[i] = new Edge(e.end, r);
			r.location.visit();
			i++;
		}
		int total = 0;
		for (Edge e : edges) {
			total += e != null ? e.length : 0;
		}
		System.out.println("Cheapest insertion length: " + total);
//		printEdges(edges);
		return edges;
	}

	int insertionGain(int[][] distances, Edge e, int id) {
		return distances[e.start.id][id] + distances[e.end.id][id] - e.length;
	}

	public Stack convexHullFinder(ArrayList<Request> requests) {
		Stack stack = new Stack();
		requests = sortByAngle(requests);		
		stack.push(requests.get(requests.size() - 1));
		stack.push(requests.get(0));
		int i = 1;
		System.out.println("Requests:" + requests.size());
		while (i < requests.size()) {	
			System.out.println(i);
			if (!isRight(requests.get(i), stack.header.data, stack.header.next.data)) {
				stack.push(requests.get(i));
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

	public ArrayList<Request> sortByAngle(ArrayList<Request> requests) {		
		if (requests.size() < 2)
			return requests;

		for (int i = 0; i < requests.size() - 1; i++) {
			for (int j = 0; j < requests.size() - i - 1; j++) {
				if (requests.get(j).location.angle > requests.get(j + 1).location.angle) {
					Request l = requests.get(j);
					requests.set(j, requests.get(j + 1));
					requests.set(j + 1, l);
				}
			}
		}
		return requests;
	}

	void print(ArrayList<Location> locations) {
		for (int i = 0; i < locations.size(); i++) {
			locations.get(i).print();
		}
	}
	
	void printRequests(ArrayList<Request> requests) {
		for (int i = 0; i < requests.size(); i++) {
			requests.get(i).location.print();
		}
	}
	
	void printEdges(Edge[] edges) {
		for (Edge e : edges) {
			if (e != null) {
				System.out.println(e.start.id + " " + e.end.id);
			} else {
				System.out.println(e);
			}
			//System.out.println("(" +e.start.location.x + "," +e.start.location.y+ ") - (" +
			//		e.end.location.x + "," + e.end.location.y + ") " + e.length);
		}
	}

	boolean isRight(Request P, Request Ptop, Request Ptop1) {
		P.location.print();
		Ptop.location.print();
		Ptop1.location.print();
		return ((Ptop.location.x - Ptop1.location.x) * (P.location.y - Ptop1.location.y)
				- (Ptop.location.y - Ptop1.location.y) * (P.location.x - Ptop1.location.x) <= 0);
	}

}