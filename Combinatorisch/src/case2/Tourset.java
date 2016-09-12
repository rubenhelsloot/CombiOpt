package case2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Tourset extends ArrayList<Tour> {
	public int length;
	Depot depot;
	
	Tourset(Depot depot) {
		super();
		this.depot = depot;
		length = 0;
	}
	
	@Override
	public boolean add(Tour t) {
		length++;
		return super.add(t);
	}
	
	@Override
	public Tour remove(int i) {
		length--;
		return super.remove(i);
	}
	
	Tourset nearestMerger() {
		if (size() == 1)
			return this;
		int dist = Integer.MAX_VALUE;
		int[] mergepoints, best;
		
		do {
			best = new int[6];
			for(int i = 0; i < this.size(); i++) {
				Tour t1 = new Tour(this.get(i));
				for (int j = i + 1; j < this.size(); j++) {
					Tour t2 = new Tour(this.get(j));
					mergepoints = t1.closestPair(t2);
					
					if (mergepoints[4] < dist && t1.merge(t2, mergepoints, 0).validate(depot)) {
						dist = mergepoints[4];
						best[0] = i;
						best[1] = j;
						System.arraycopy(mergepoints, 0, best, 2, 4);
					}
					t1 = new Tour(this.get(i)); //reset
				}
			
			}
			
			Tour t1 = new Tour(this.get(best[0]));
			Tour t2 = new Tour(this.get(best[1]));
			t1.merge(t2, best, 2);
			add(best[0], t1);
			remove(best[1]);
		} while (best[0] != 0);

		return this;
	}
	
	Tourset limitedLS(long distanceCost, long vehicleDayCost) {
		long costs = 0;
		for (Tour t : this) {
			costs += t.costs(distanceCost, vehicleDayCost);
		}
		System.out.println(costs);
		return this;
	}
	
	Tourset clean() {
		Iterator<Tour> itr = this.iterator();
		while (itr.hasNext()) {
			Tour t = itr.next();
			if (t.length() == 0) {
				itr.remove();
			} else {
				t.removeDuplicates();
			}
		}
		return this;
	}
	
	boolean validateAll(Depot depot) {
		Iterator<Tour> itr = this.iterator();
		Map<Integer, Integer> tools = new HashMap<>();
		Map<Integer, Integer> max = new HashMap<>();
		while (itr.hasNext()) {
			Tour t = itr.next();
			t.removeDuplicates();
			if (t.isEmpty()) {
				itr.remove();
				continue;
			}
			if (!t.validate(depot))
				return false;
			for (Edge e : t) {
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

	boolean validate(Depot depot) {
		for (Tour t : this) {
			if (!t.validate(depot))
				return false;
		}
		return true;
	}
}
