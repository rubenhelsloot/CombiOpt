package case2;

import java.util.ArrayList;

public class Day {

	public ArrayList<Location> must;
	public ArrayList<Location> may;
	public ArrayList<Location> all;
	public int dayId;
	private Depot depot;
	boolean debug;

	Day(int dayId, Depot depot) {
		this.depot = depot;
		this.dayId = dayId;
		if (dayId == 2 || dayId == 3)
			debug = true;
		must = new ArrayList<>();
		may = new ArrayList<>();
		all = new ArrayList<>();
	}

	void init(ArrayList<Location> locations) {
		all = locations;
		if (debug)
			System.out.println("this is breakpoint");
		for (Location l : locations) {
			System.out.print(l.r.id);
			if (l.r.delivered)
				System.out.print("-" + l.r.remaining);
			System.out.print(" ");
			if (l.classifyRequest(dayId)) {
				must.add(l);
			} else {
				may.add(l);
			}
		}
		System.out.print("\n");
	}

	void scheduleMusts(int[][] distances) {
		must.add(depot.location);
		System.out.print("Must: ");
		for (Location l : must) {
			System.out.print(l.id + " ");
		}
		System.out.print("\n");
		Tour t = cheapestInsertion(convexHullFinder(must), must, distances);
		t.cycle(depot.location);

		// System.out.println((t.weight() < depot.maxCap) + " " + (t.length() <
		// depot.maxDist));

		Vehicle v = depot.getVehicle();
		v.addTour(t);

		all.add(depot.location);
		new Scatterplot(all, t);
		all.remove(depot.location);

		System.out.print("Delivering: ");
		for (Edge e : t.tour) {
			if (!e.end.isDepot) {
				System.out.print(e.end.id + " ");
				e.end.r.deliver();
			}
		}
		System.out.print("\n");
	}

	Tour cheapestInsertion(Stack convexHull, ArrayList<Location> locations, int[][] distances) {
		Tour tour = new Tour();
		tour.tour = new ArrayList<Edge>();
		Node current = convexHull.header;
		int i = 0;

		if (debug)
			convexHull.print();

		while (current.next != null) {
			tour.tour.add(new Edge(current.data, current.next.data));
			current.data.visit();
			current.next.data.visit();
			current = current.next;
			i++;

			if (!tour.contains(depot.location))
				depot.location.visited = false;
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
			tour.tour.add(replace + 1, new Edge(l, e.end));
			l.visit();
			i++;
		}
		tour.print();
		System.out.println("Cheapest insertion length: " + tour.length());

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
		for (int i = 1; i < locations.size(); i += 0) {
			if (!isRight(locations.get(i), stack.header.data, stack.header.next.data)) {
				// if (debug) System.out.println("Size: " + stack.size());
				stack.push(locations.get(i));
				// if (debug) stack.print();
				i++;
			} else {
				Location l = stack.pop();
				// if (debug) System.out.println("Popped: " + l.id);
				if (stack.size() == 1) {
					stack.push(locations.get(i));
					stack.push(l);
					i++;
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