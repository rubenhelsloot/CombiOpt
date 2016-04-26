package case2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Day {

	public ArrayList<Location> must;
	public ArrayList<Location> may;
	public int dayId;
	private Depot depot;

	Day(int dayId, Depot depot) {
		this.depot = depot;
		this.dayId = dayId;
		must = new ArrayList<>();
		may = new ArrayList<>();
	}

	void init(ArrayList<Location> locations) {
		for(Location l : locations) {
			if(l.classifyRequests(dayId)) {
				must.add(l);
			} else if (l.may.size() > 0) {
				may.add(l);
			}
		}
	}

	void scheduleMusts(int maxDistance, int[][] distances) {
		ArrayList<Request> pickups = new ArrayList<>();
		ArrayList<Request> deliveries = new ArrayList<>();
		for (Location l : must) {
			for(Request r : l.must) {
				if (r.delivered) {
					pickups.add(r);
				} else {
					deliveries.add(r);
				}
			}
		}
		must.add(depot.location);
//		Set<Location> s = new HashSet<Location>();
//		for (Location l : must) {
			
//		}
		Stack convexHull = convexHullFinder(must);
		cheapestInsertion(convexHull, must, distances);
	}

	void recursiveSchedule(ArrayList<Request> pickups, ArrayList<Request> deliveries) {

		// if(pickups.size() > 0) {
		// Request r = pickups.get(0);
		// deliveries = sortByLocation(deliveries, r.location);
		//
		// }
	}
	
	List<Location> cheapestInsertion(Set<Location> locations) {
		List<Location> route = new ArrayList<Location>(locations.size() + 1);
		Iterator<Location> itr = locations.iterator();
		if (itr.hasNext()) {
			Location l = itr.next();
			int insertion = 0;
			while (!locations.isEmpty()) {
				route.add(insertion, l);
				if (locations.remove(l) && locations.isEmpty()) {
					break;
				}
				Location nearest = null;
				insertion = -1;
				double min = Double.POSITIVE_INFINITY;
				Location n0 = route.get(route.size() - 1);
				for (int i = 0; i < route.size(); i++) {
					Location n1 = route.get(i);
					for (Location n2 : locations) {
						double distance = Math.abs(n0.distance(n2) + n2.distance(n1) - n0.distance(n1));
						if (min > distance) {
							min = distance;
							insertion = i;
							nearest = n2;
						}
					}
					n0 = n1;
				}
				assert insertion != -1 && nearest != null;
				l = nearest;
			}
		}
		return route;
	}

	Edge[] cheapestInsertion(Stack convexHull, ArrayList<Location> locations, int[][] distances) {
		Edge[] edges = new Edge[locations.size()];
		Node current = convexHull.header;
		int i = 0;
		
		convexHull.print();
		
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
			
			while (locations.get(a).visited) {
				a++;
				if (a == locations.size()) {
					System.out.println("Hull contained all nodes");
					return edges;
				}
			}
			
			Location l = locations.get(a);
			double dist = insertionGain(distances, e, depot.location.id);
			
			for (int j = a + 1; j < locations.size(); j++) {
				if (!locations.get(j).visited) {
					for (int k = 0; k < i; k++) {
						if (insertionGain(distances, edges[k], locations.get(j).id) < dist) {
							dist = insertionGain(distances, edges[k], locations.get(j).id);
							e = edges[k];
							l = locations.get(j);
							replace = k;
						}
					}
				}
			}
			
			edges[replace] = new Edge(e.start, l);
			edges[i] = new Edge(e.end, l);
			l.visit();
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

	public Stack convexHullFinder(ArrayList<Location> locations) {
		Stack stack = new Stack();
		locations = sortByAngle(locations);
		stack.push(locations.get(locations.size() - 1));
		stack.push(locations.get(0));
		System.out.println("Locations:" + locations.size());
		for(int i = 1; i < locations.size(); i+=0) {	
			System.out.println("Location no. " + i + " ("+stack.size()+")");
			if (!isRight(locations.get(i), stack.header.data, stack.header.next.data)) {
				stack.push(locations.get(i));
				i++;
			} else {
				Location l = stack.pop();
				if (stack.size() == 1) {
					stack.push(locations.get(i));
					locations.set(i, l);
				}
			}
		}
		return stack;
	}

	ArrayList<Location> sortByLocation(ArrayList<Location> locations, Location l) {
		if (locations.size() < 2)
			return locations;

		for (int i = 0; i < locations.size() - 1; i++) {
			for (int j = 0; j < locations.size() - i - 1; j++) {
				if (locations.get(j).distance(l) > locations.get(j + 1).distance(l)) {
					Location l1 = locations.get(j);
					locations.set(j, locations.get(j + 1));
					locations.set(j + 1, l1);
				}
			}
		}
		return locations;
	}

	public ArrayList<Location> sortByAngle(ArrayList<Location> locations) {		
		if (locations.size() < 2) return locations;

		for (int i = 0; i < locations.size() - 1; i++) {
			for (int j = 0; j < locations.size() - i - 1; j++) {
				if (locations.get(j).angle > locations.get(j + 1).angle) {
					Location l = locations.get(j);
					locations.set(j, locations.get(j + 1));
					locations.set(j + 1, l);
				}
			}
		}
		return locations;
	}

	void printLocations(ArrayList<Location> locations) {
		for (int i = 0; i < locations.size(); i++) {
			locations.get(i).print();
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

	boolean isRight(Location P, Location Ptop, Location Ptop1) {
		return ((Ptop.x - Ptop1.x) * (P.y - Ptop1.y) - (Ptop.y - Ptop1.y) * (P.x - Ptop1.x) <= 0);
	}

}