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
			if(l.classifyRequest(dayId)) {
				must.add(l);
			} else {
				may.add(l);
			}
		}
	}

	void scheduleMusts(int maxDistance, int[][] distances) {
		must.add(depot.location);
		Tour t = cheapestInsertion(convexHullFinder(must), must, distances);
		t.print();
		t.cycle(depot.location.id);
		System.out.println(" ");
		t.print();
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

	Tour cheapestInsertion(Stack convexHull, ArrayList<Location> locations, int[][] distances) {
		Tour tour = new Tour();
		tour.tour = new ArrayList<Edge>();
		Node current = convexHull.header;
		int i = 0;
		
		convexHull.print();
		
		while (current.next != null) {
			tour.tour.add(new Edge(current.data, current.next.data));
			current.data.visit();
			current.next.data.visit();
			current = current.next;
			i++;
		}

		while (i < locations.size()) {
			Edge e = tour.tour.get(0);
			int replace = 0;
			int a = 0;
			
			while (locations.get(a).visited) {
				a++;
				if (a == locations.size()) {
					System.out.println("Hull contained all nodes");
					return tour;
				}
			}
			
			Location l = locations.get(a);
			double dist = insertionGain(distances, e, depot.location.id);
			
			for (int j = a + 1; j < locations.size(); j++) {
				if (!locations.get(j).visited) {
					for (int k = 0; k < i; k++) {
						if (insertionGain(distances, tour.tour.get(k), locations.get(j).id) < dist) {
							dist = insertionGain(distances, tour.tour.get(k), locations.get(j).id);
							e = tour.tour.get(k);
							l = locations.get(j);
							replace = k;
						}
					}
				}
			}
			
			tour.tour.set(replace, new Edge(e.start, l));
			if(tour.tour.size() > i) {
				tour.tour.set(i, new Edge(l, e.end));
			} else {
				tour.tour.add(new Edge(l, e.end));
			}
			l.visit();
			i++;
		}
		int total = 0;
		for (Edge e : tour.tour) {
			total += e != null ? e.length : 0;
		}
		System.out.println("Cheapest insertion length: " + total);
		return tour;
	}

	int insertionGain(int[][] distances, Edge e, int id) {
		return distances[e.start.id][id] + distances[e.end.id][id] - e.length;
	}

	public Stack convexHullFinder(ArrayList<Location> locations) {
		Stack stack = new Stack();
		locations = sortByAngle(locations);
		stack.push(locations.get(locations.size() - 1));
		stack.push(locations.get(0));
		for(int i = 1; i < locations.size(); i+=0) {	
			if (!isRight(locations.get(i), stack.header.data, stack.header.next.data)) {
				System.out.println("Size: " + stack.size());
				stack.print();
				stack.push(locations.get(i));
				i++;
			} else {
				System.out.println("To pop: " + stack.peek().id);
				Location l = stack.pop();
				System.out.println("Popped: " + l.id);
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

	boolean isRight(Location P, Location Ptop, Location Ptop1) {
		return ((Ptop.x - Ptop1.x) * (P.y - Ptop1.y) - (Ptop.y - Ptop1.y) * (P.x - Ptop1.x) <= 0);
	}

}