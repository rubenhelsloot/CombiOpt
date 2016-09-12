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
	ArrayList<Location> must;
	ArrayList<Location> may;
	ArrayList<Location> all;
	Tourset tours;
	int dayId;
	Depot depot;
	boolean debug;
	int[][] distances;
	long vehicleCost, vehicleDayCost, distanceCost;

	Day(int dayId, Depot depot, long vehicleCost, long vehicleDayCost, long distanceCost) {
		try {
			bf = new BufferedWriter(new FileWriter("Debug/Logday" + dayId + ".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.depot = depot;
		this.dayId = dayId;
		debug = (dayId == 3);
		must = new ArrayList<>();
		may = new ArrayList<>();
		all = new ArrayList<>();
		this.vehicleCost = vehicleCost;
		this.vehicleDayCost = vehicleDayCost;
		this.distanceCost = distanceCost;
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
		tours = new Tourset(depot);
		if (must.size() == 0)
			return;

		// for (Location l : must) {
		// Tour t = new Tour();
		// t.tour.add(new Edge(depot.location, l));
		// t.tour.add(new Edge(l, depot.location));
		//
		// if (t.validate(depot)) {
		// tours.add(t);
		// }
		// }

		// ArrayList<ArrayList<Location>> clusters = new KMeans(1, must,
		// depot.location).getClusters();
		// for (ArrayList<Location> c : clusters) {
		// c.add(depot.location);
		// tours.add(cheapestInsertion(convexHullFinder(c), c,
		// distances).cycle(depot.location));
		// }
		must.add(depot.location);
		tours.add(cheapestInsertion(convexHullFinder(must), must, distances).cycle(depot.location));

		if (tours.validateAll(depot)) {
		}
		// else if (validate(tours, depot)) {
		// init(all);
		// scheduleMusts(distances);
		// return;
		// }
		else {
			while (!tours.validateAll(depot)) {
				Iterator<Tour> itr = tours.iterator();
				Tour prev = null;
				while (itr.hasNext()) {
					Tour tour = itr.next().makeValid(depot);
					if (!tour.validate(depot)) {
						itr.remove();

						ArrayList<Location> cluster = new ArrayList<>();
						for (Edge e : tour) {
							e.end.reset();
							if (!e.end.isDepot)
								cluster.add(e.end);
						}

						ArrayList<ArrayList<Location>> clusters = new ArrayList<>();
						if (cluster.size() == 2) {
							for (Location l : cluster) {
								ArrayList<Location> al = new ArrayList<>();
								al.add(l);
								clusters.add(al);
							}
						} else {
							clusters = new KMeans(2, cluster, depot.location).getClusters();
							boolean success = true;
							for (ArrayList<Location> c : clusters) {
								if (c.size() == 0)
									success = false;
							}
							if (!success) {
								clusters = new KMeans(2, cluster, depot.location).getClusters();
							}
						}

						for (ArrayList<Location> c : clusters) {
							c.add(depot.location);
							Tour newTour = cheapestInsertion(convexHullFinder(c), c, distances);
							tours.add(newTour.cycle(depot.location));
						}

						itr = tours.iterator();
						while (itr.hasNext() && itr.next() != prev) {
						}
					} else {
						prev = tour;
					}
				}
			}
		}

		tours.nearestMerger().limitedLS(distanceCost, vehicleDayCost).clean();

		// for (Location l : may) {
		// boolean added = false;
		// ArrayList<Location> al = new ArrayList<>();
		// Tourset toursClone = new ArrayList<>();
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
//		 all.add(depot.location);
//		 new Scatterplot(all, tours, dayId);
//		 all.remove(depot.location);

//		tours = cleanTours(tours);
		assignTours();
	}

	void assignTours() {
		for (Tour tour : tours) {
			Vehicle v = depot.getVehicle();

			v.addTour(tour);

			String line = "Delivering: ";
			for (Edge e : tour) {
				if (!e.end.isDepot) {
					line += e.end.id + " ";
					e.end.r.deliver();
				}
			}

			try {
				bf.write(line);
				bf.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	Tour cheapestInsertion(Stack convexHull, ArrayList<Location> locations, int[][] distances) {
		Tour tour = new Tour();
		Node current = convexHull.header;

		while (current.next != null) {
			tour.add(new Edge(current.data, current.next.data));
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

		tour.cheapestInsertion(locations, distances, depot).removeDuplicates();
//		tour.print(bf);

		return tour;
	}

	Stack convexHullFinder(ArrayList<Location> locations) {
		Stack stack = new Stack();

		if (locations.size() == 2) {
			stack.push(locations.get(locations.size() - 1));
			stack.push(locations.get(0));
			return stack;
		}

		Set<Integer> uniqueLIds = new HashSet<>();
		for (Location l : locations) {
			uniqueLIds.add(l.id);
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