package case2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Day {

	BufferedWriter bf;
	public ArrayList<Location> must;
	public ArrayList<Location> may;
	public ArrayList<Location> all;
	public ArrayList<Tour> tours;
	public int dayId;
	private Depot depot;
	boolean debug;
	int[][] distances;

	Day(int dayId, Depot depot) {
//		try {
//			bf = new BufferedWriter(new FileWriter("Debug/Logday" + dayId + ".txt"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		this.depot = depot;
		this.dayId = dayId;
		debug = (dayId == 3);
		must = new ArrayList<>();
		may = new ArrayList<>();
		all = new ArrayList<>();
	}

	void init(ArrayList<Location> locations) {
		all = locations;
		for (Location l : locations) {
			// System.out.print(l.r.id);
			// if (l.r.delivered)
			// System.out.print("-" + l.r.remaining);
			// System.out.print(" ");
			if (l.classifyMusts(dayId)) {
				must.add(l);
			} else if (l.classifyMays(dayId)) {
				may.add(l);
			}
		}
		// System.out.print("\n");
	}

	void scheduleMusts(int[][] distances) {
		this.distances = distances;
		tours = new ArrayList<>();

		for (Location l : must) {
			Tour t = new Tour();
			t.tour.add(new Edge(depot.location, l));
			t.tour.add(new Edge(l, depot.location));

			if (t.validate(depot)) {
				tours.add(t);
			}
		}

		// if (vehicles > 1) {
		// ArrayList<ArrayList<Location>> clusters = new KMeans(vehicles, must,
		// depot.location).getClusters();
		// for (ArrayList<Location> c : clusters) {
		// c.add(depot.location);
		// tours.add(cheapestInsertion(convexHullFinder(c), c,
		// distances).cycle(depot.location));
		// }
		// } else {
		// if (must.size() == 0)
		// return;
		// must.add(depot.location);
		// tours.add(cheapestInsertion(convexHullFinder(must), must,
		// distances).cycle(depot.location));
		// }

		// if (!validateAll(tours, depot)) {
		// success = true;
		// } else if (validate(tours, depot)) {
		// init(all);
		// scheduleMusts(distances);
		// return;
		// } else {
		// for (Tour tour : tours) {
		// int i = 0;
		// while (!tour.validate(depot) && i < 100) {
		// ArrayList<Location> cluster = new ArrayList<>();
		// for (Edge e : tour.tour) {
		// e.end.reset();
		// if (!e.end.isDepot)
		// cluster.add(e.end);
		// }
		// ArrayList<ArrayList<Location>> clusters = new KMeans(2, cluster,
		// depot.location).getClusters();
		// for (ArrayList<Location> c : clusters) {
		// c.add(depot.location);
		// tours.add(cheapestInsertion(convexHullFinder(c), c,
		// distances).cycle(depot.location));
		// }
		// i++;
		// }

		// if (i == 100) {
		// init(all);
		// scheduleMusts(distances);
		// return; // Retry
		// }
		// }
		// vehicles = 0;
		// for (Tour tour : tours) {
		// vehicles += Math.max((int) Math.ceil(tour.length() * 1.0 /
		// depot.maxDist),
		// (int) Math.ceil(tour.weight() * 1.0 / depot.maxCap));
		// if (previousVehicles == vehicles)
		// vehicles++;
		// for (Edge e : tour.tour) {
		// e.end.reset();
		// }
		// }
		// }

		 tours = mergeTours(tours);

		// for (Location l : may) {
		// boolean added = false;
		// ArrayList<Location> al = new ArrayList<>();
		// ArrayList<Tour> toursClone = new ArrayList<>();
		// al.add(l);
		// for (Tour t : tours) {
		// toursClone.add(new Tour(t));
		// }
		// int i = 0;
		// while (i < toursClone.size() && !added) {
		// Tour tClone = toursClone.get(i);
		// tClone = cheapestInsertion(tClone, al,
		// distances).cycle(depot.location);
		//
		// if (validateAll(toursClone, depot)) {
		// added = true;
		// tours.set(i, tClone);
		// } else {
		// toursClone.set(i, tours.get(i));
		// i++;
		// }
		// }
		// }
		// all.add(depot.location);
		// new Scatterplot(all, tours, dayId);
		// all.remove(depot.location);

		for (Tour tour : tours) {
			Vehicle v = depot.getVehicle();

			v.addTour(tour);

			String line = "Delivering: ";
			for (Edge e : tour.tour) {
				if (!e.end.isDepot) {
					line += e.end.id + " ";
					e.end.r.deliver();
				}
			}
			//
			// try {
			// bf.write(line);
			// bf.newLine();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}

	}

	ArrayList<Tour> mergeTours(ArrayList<Tour> tours) {
		// int i = 0;
		// while (i < tours.size()) {
		// Tour t = new Tour(tours.get(i));
		// for (int j = i; j < tours.size(); j++) {
		// t.merge(new Tour(tours.get(j)));
		// if (t.validate(depot)) {
		// tours.set(i, t);
		// tours.remove(j);
		// }
		// }
		// i++;
		// }
		// return tours;

		int i = 0;
		while (i < tours.size()) {
			for (int j = i + 1; j < tours.size(); j++) {
				Tour tClone = new Tour(tours.get(i));
				tClone.add(new Tour(tours.get(j)));
				if (tClone.validate(depot)) {
					tours.set(i, tClone);
					tours.remove(j);
				} else {
					tClone = new Tour(tours.get(i));
				}
			}
			i++;
		}

		return tours;
	}

	Tour cheapestInsertion(Stack convexHull, ArrayList<Location> locations, int[][] distances) {
		Tour tour = new Tour();
		tour.tour = new ArrayList<Edge>();
		Node current = convexHull.header;

		while (current.next != null) {
			tour.tour.add(new Edge(current.data, current.next.data));
			current.data.visit();
			current.next.data.visit();
			current = current.next;
		}

		if (!tour.contains(depot.location))
			depot.location.visited = false;

		Iterator<Location> itr = locations.iterator();
		while (itr.hasNext()) {
			if (tour.contains(itr.next())) {
				itr.remove();
			}
		}

		tour = cheapestInsertion(tour, locations, distances);
		// boolean doBreak = false;
		// while (tour.size() < locations.size()) {
		// Edge e = tour.tour.get(0);
		// int replace = 0;
		//
		// while (locations.get(a).visited) {
		// a++;
		// if (a == locations.size()) {
		// doBreak = true;
		// System.out.println("BREAKING");
		// break;
		// }
		// }
		//
		// if (doBreak)
		// break;
		//
		// Location l = locations.get(a);
		// double dist = insertionGain(distances, e, depot.location.id);
		//
		// for (int j = a; j < locations.size(); j++) {
		// if (!locations.get(j).visited) {
		// for (int k = 0; k < tour.size(); k++) {
		//
		// if (insertionGain(distances, tour.tour.get(k), locations.get(j).id) <
		// dist) {
		// dist = insertionGain(distances, tour.tour.get(k),
		// locations.get(j).id);
		// e = tour.tour.get(k);
		// l = locations.get(j);
		// replace = k;
		// }
		// }
		// }
		// }
		//
		// if (tour.tour.get(replace).start.isDepot &&
		// tour.tour.get(replace).end.isDepot) {
		// System.out.println("Removing " + tour.tour.get(replace).print());
		// System.out.println(tour.print());
		// tour.tour.remove(replace);
		// continue;
		// }
		//
		// System.out.println("First");
		// System.out.println(tour.print());
		// tour.tour.set(replace, new Edge(e.start, l));
		// tour.tour.add(replace + 1, new Edge(l, e.end));
		// l.visit();
		// System.out.println(tour.print());
		// }
		tour.removeDuplicates();
		tour.print(bf);
		System.out.println(tour.print());
		System.out.println("Cheapest insertion length: " + tour.length());

		return tour;
	}

	Tour cheapestInsertion(Tour tour, ArrayList<Location> locations, int[][] distances) {
		int i = 0;
		while (i < locations.size()) {
			Edge e = tour.tour.get(0);
			int replace = 0;
			int a = 0;

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

			System.out.println("Second");
			System.out.println(tour.print());
			tour.tour.set(replace, new Edge(e.start, l));
			tour.tour.add(replace + 1, new Edge(l, e.end));
			l.visit();
			i++;
			System.out.println(tour.print());
		}

		tour.removeDuplicates();

		return tour;
	}

	Stack convexHullFinder(ArrayList<Location> locations) {
		Stack stack = new Stack();

		if (locations.size() == 2) {
			stack.push(locations.get(locations.size() - 1));
			stack.push(locations.get(0));
			// stack.push(depot.location);
			return stack;
		}

		Set<Integer> uniqueLIds = new HashSet<>();
		for (Location l : locations) {
			uniqueLIds.add(l.id);
		}
		boolean debug = false;

		if (uniqueLIds.size() < locations.size() && locations.size() == 3) {
			debug = true;
			// stack.push(depot.location);
		}

		for (Location l : locations)
			if (l.id == 805 && dayId == 18) {
				debug = true;
				System.out.println("DEBUG!");
			}

		locations = sortByAngle(locations);
		stack.push(locations.get(locations.size() - 1));
		stack.push(locations.get(0));
		int i = 1;
		while (i < locations.size()) {
			if (!isRight(locations.get(i), stack.header.data, stack.header.next.data)) {
				stack.push(locations.get(i));
				i++;
			} else {
				Location l = stack.pop();
				if (stack.size() == 1) {
					stack.push(locations.get(i));
					stack.push(l);
					i++;
				}
			}
		}
		if (debug) {
			System.out.println(stack.size());
			stack.print();

			if (stack.contains(depot.location)) {
				stack.print();
			} else {
				stack.print();
			}
		}

		if (stack.contains(depot.location)) {
			stack.print();
		} else {
			stack.print();
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

	ArrayList<Location> sortByAngle(ArrayList<Location> locations) {
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
			locations.get(i).print(bf);
		}
	}

	boolean isRight(Location P, Location Ptop, Location Ptop1) {
		int result = ((Ptop.x - Ptop1.x) * (P.y - Ptop1.y) - (Ptop.y - Ptop1.y) * (P.x - Ptop1.x));
		return result < 0;
	}

	int insertionGain(int[][] distances, Edge e, int id) {
		return distances[e.start.id][id] + distances[e.end.id][id] - e.length;
	}

	boolean validateAll(ArrayList<Tour> tours, Depot depot) {
		Iterator<Tour> itr = tours.iterator();
		Map<Integer, Integer> tools = new HashMap<>();
		Map<Integer, Integer> max = new HashMap<>();
		while (itr.hasNext()) {
			Tour t = itr.next();
			t.removeDuplicates();
			if (t.tour.isEmpty()) {
				itr.remove();
				continue;
			}
			if (!t.validate(depot))
				return false;
			for (Edge e : t.tour) {
				if (!e.end.isDepot && !e.end.r.delivered) {
					tools.put(e.end.r.type, tools.getOrDefault(e.end.r.type, 0) + e.end.r.amount);
					max.put(e.end.r.type,
							Math.max(tools.getOrDefault(e.end.r.type, 0), max.getOrDefault(e.end.r.type, 0)));
				} else if (!e.end.isDepot && e.end.r.delivered) {
					tools.put(e.end.r.type, tools.getOrDefault(e.end.r.type, 0) - e.end.r.amount);
				}
			}
		}

		return true;
	}

	boolean validate(ArrayList<Tour> tours, Depot depot) {
		for (Tour t : tours) {
			if (!t.validate(depot))
				return false;
		}
		return true;
	}

	Location findBottomRight(ArrayList<Location> locations) {
		Location current = locations.get(0);
		for (Location l : locations) {
			if ((l.y > current.y) || (l.y == current.y && l.x > current.x)) {
				current = l;
			}
		}
		return current;
	}
}