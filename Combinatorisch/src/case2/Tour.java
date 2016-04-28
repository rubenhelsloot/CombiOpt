package case2;

import java.util.ArrayList;
import java.util.Iterator;

public class Tour {
	ArrayList<Edge> tour;
	
	Tour() {
		tour = new ArrayList<>();
	}
	
	Tour cycle(int depotId) {
		int max = tour.size();
		int i = 0;
		while(tour.get(0).start.id != depotId && i < max) {
			Edge e = tour.remove(0);
			tour.add(e);
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
			//System.out.println("(" +e.start.location.x + "," +e.start.location.y+ ") - (" +
			//		e.end.location.x + "," + e.end.location.y + ") " + e.length);
		}
	}
}
