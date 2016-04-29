package case2;

import java.util.ArrayList;

public class Tour {
	ArrayList<Edge> tour;

	Tour() {
		tour = new ArrayList<>();
	}

	Tour cycle() {
		int max = tour.size();
		int i = 0;

		while (/*tour.get(0).start.id != depot.location.id &&*/ i < max) {
			Edge e = tour.get(0);
			tour.add(e);
			System.out.println(tour.get(0).start.id + " - " + tour.get(0).end.id);
			tour.remove(0);

			i++;
		}

		return this;
	}

	void print() {
		for (Edge e : tour) {
			if (e != null) {
				e.print();
			} else {
				System.out.println(e);
			}

			// System.out.println("(" +e.start.location.x + ","
			// +e.start.location.y+ ") - (" +
			// e.end.location.x + "," + e.end.location.y + ") " + e.length);
		}
	}

	boolean contains(Location l) {
		for (Edge e : tour) {
			if (l.id == e.start.id || l.id == e.end.id) {
				return true;
			}
		}
		return false;
	}
}
