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
		for (Location l : locations) {
			System.out.print(l.r.id);
			if (l.r.delivered)
				System.out.print("-" + l.r.remaining);
			System.out.print(" ");
			if (l.classifyRequest(dayId)) {
				must.add(l);
				
			} else if (l.classifyMay(dayId)){
				may.add(l);
			}
		}
		System.out.print("\n");
		
	}

	void scheduleMusts(int[][] distances) {
		System.out.print(" MUST LIST: ");
		for(int i=0 ; i<must.size(); i++){
			System.out.print(must.get(i).r.locationId+" ");
		}
		System.out.print("\n");
		System.out.print(" MAY LIST: ");
		for(int i=0 ; i<may.size(); i++){
			System.out.print(may.get(i).r.locationId+" ");
		}
		System.out.print("\n");
		must.add(depot.location);
		
		ArrayList<Tour> t = new ArrayList<>();
		t.add(cheapestInsertion(convexHullFinder(must), must, distances).cycle(depot.location));
		/*int k =0;
		ArrayList<Location> temp = new ArrayList<Location>();
	
		while(validateAll(t, depot.maxCap, depot.maxDist)){
			temp.add(may.get(k));
			t.add(cheapestInsertion(convexHullFinder(temp), temp, distances).cycle(depot.location));
			System.out.println(" ADDED MAY TO ROUTE --------------------------------------------------------------");
			k++;
		}
		*/
		if (validateAll(t, depot.maxCap, depot.maxDist)) {
			for (Tour tour : t) {
				Vehicle v = depot.getVehicle();
				v.addTour(tour);
			
			all.add(depot.location);
			new Scatterplot(all, t, dayId);
			all.remove(depot.location);
			
			System.out.print("Delivering: ");
			for (Edge e : tour.tour) {
				if (!e.end.isDepot) {
					System.out.print(e.end.id + " ");
					e.end.r.deliver();
				}
			}
			System.out.print("\n");
			}
		} else {
			int vehicles=0;
			for (Tour tour : t) {
			System.out.println("Route unfit " + (tour.length() < depot.maxDist) + (tour.weight() < depot.maxCap));
			vehicles = Math.max((int) Math.ceil(tour.length() * 1.0 / depot.maxDist),
					(int) Math.ceil(tour.weight() * 1.0 / depot.maxCap));
			}
			ArrayList<ArrayList<Location>> clusters = new KMeans(vehicles, must, depot.location).getClusters();
			ArrayList<Tour> tours = new ArrayList<>();
			for (ArrayList<Location> c : clusters) {
				if (c.size() == 1) {
					System.out.println("HERE! OVER HERE!");
				}
				c.add(depot.location);
				tours.add(cheapestInsertion(convexHullFinder(c), c, distances).cycle(depot.location));
			}
			
			while(!validateAll(tours, depot.maxCap, depot.maxDist)) {
				vehicles = 0;
				for (Tour tour : tours) {
					System.out.println("Subroute unfit length: " + (tour.length() < depot.maxDist) + " weight: " + (tour.weight() < depot.maxCap));
					vehicles += Math.max((int) Math.ceil(tour.length() * 1.0 / depot.maxDist),
							(int) Math.ceil(tour.weight() * 1.0 / depot.maxCap));
				}
				System.out.println("Try with " + vehicles + " vehicles");
				clusters = new KMeans(vehicles, must, depot.location).getClusters();
				tours = new ArrayList<>();
				for (ArrayList<Location> c : clusters) {
					c.add(depot.location);
					tours.add(cheapestInsertion(convexHullFinder(c), c, distances).cycle(depot.location));
				}
			}
			
			all.add(depot.location);
			new Scatterplot(all, tours, dayId);
			all.remove(depot.location);
			
			for (Tour tour : tours) {
				Vehicle v = depot.getVehicle();
				v.addTour(tour);
				
				System.out.print("Delivering: ");
				for (Edge e : tour.tour) {
					if (!e.end.isDepot) {
						System.out.print(e.end.id + " ");
						e.end.r.deliver();
					}
				}
				System.out.print("\n");
			}
		}
	}
	
	
	
	
	
	
	void scheduleMays(int[][] distances){
		
		
		
		if(!may.isEmpty()){
			System.out.println("START MAYS-SCHEDULE");
		System.out.print(" MUST LIST: ");
		for(int i=0 ; i<must.size(); i++){
			System.out.print(must.get(i).r.locationId+" ");
		}
		System.out.print("\n");
		System.out.print(" MAY LIST: ");
		for(int i=0 ; i<may.size(); i++){
			System.out.print(may.get(i).r.locationId+" ");
		}
		System.out.print("\n");
		
		may.add(depot.location);
		Tour t = cheapestInsertion(convexHullFinder(may), may, distances).cycle(depot.location);

		if (t.length() < depot.maxDist && t.weight() < depot.maxCap) {
			Vehicle v = depot.getVehicle();
			v.addTour(t);
			all.add(depot.location);
			new Scatterplot(all, t, dayId);
			all.remove(depot.location);
			
			System.out.print("Delivering: ");
			for (Edge e : t.tour) {
				if (!e.end.isDepot) {
					System.out.print(e.end.id + " ");
					e.end.r.deliver();
				}
			}
			System.out.print("\n");
		} else {
			System.out.println("Route unfit " + (t.length() < depot.maxDist) + (t.weight() < depot.maxCap));
			int vehicles = Math.max((int) Math.ceil(t.length() * 1.0 / depot.maxDist),
					(int) Math.ceil(t.weight() * 1.0 / depot.maxCap));
			ArrayList<ArrayList<Location>> clusters = new KMeans(vehicles, may, depot.location).getClusters();
			ArrayList<Tour> tours = new ArrayList<>();
			for (ArrayList<Location> c : clusters) {
				if (c.size() == 1) {
					System.out.println("HERE! OVER HERE!");
				}
				c.add(depot.location);
				tours.add(cheapestInsertion(convexHullFinder(c), c, distances).cycle(depot.location));
			}
			
			while(!validateAll(tours, depot.maxCap, depot.maxDist)) {
				vehicles = 0;
				for (Tour tour : tours) {
					System.out.println("Subroute unfit length: " + (tour.length() < depot.maxDist) + " weight: " + (tour.weight() < depot.maxCap));
					vehicles += Math.max((int) Math.ceil(tour.length() * 1.0 / depot.maxDist),
							(int) Math.ceil(tour.weight() * 1.0 / depot.maxCap));
				}
				System.out.println("Try with " + vehicles + " vehicles");
				clusters = new KMeans(vehicles, may, depot.location).getClusters();
				tours = new ArrayList<>();
				for (ArrayList<Location> c : clusters) {
					c.add(depot.location);
					tours.add(cheapestInsertion(convexHullFinder(c), c, distances).cycle(depot.location));
				}
			}
			
			all.add(depot.location);
			new Scatterplot(all, tours, dayId);
			all.remove(depot.location);
			
			for (Tour tour : tours) {
				Vehicle v = depot.getVehicle();
				v.addTour(tour);
				
				System.out.print("Delivering: ");
				for (Edge e : tour.tour) {
					if (!e.end.isDepot) {
						System.out.print(e.end.id + " ");
						e.end.r.deliver();
					}
				}
				
			}
		}
		System.out.println("END MAYS SCHEDULE");
		} else {
			System.out.println("NO MAYS LEFT TO SCHEDULE");
		}
		
		
		
	}

	Tour cheapestInsertion(Stack convexHull, ArrayList<Location> locations, int[][] distances) {
		if (convexHull.size() == 3 && dayId == 4) {
			System.out.println("Hello world");
		}
		
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
					System.out.print("Hull contained all nodes: ");
					tour.print();
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

	public Stack convexHullFinder(ArrayList<Location> locations) {
		Stack stack = new Stack();
		
		if (locations.size() == 2) {
			stack.push(depot.location);
			stack.push(locations.get(0));
			stack.push(depot.location);
			return stack;
		}
		
		locations = sortByAngle(locations);
		stack.push(locations.get(locations.size() - 1));
		stack.push(locations.get(0));
		int i = 1;
		while (i < locations.size()) {
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

	int insertionGain(int[][] distances, Edge e, int id) {
		return distances[e.start.id][id] + distances[e.end.id][id] - e.length;
	}
	
	boolean validateAll(ArrayList<Tour> tours, int maxCap, int maxDist) {
		for(Tour t : tours) {
			if (!t.validate(maxCap, maxDist))
				return false;
		}
		return true;
	}
}