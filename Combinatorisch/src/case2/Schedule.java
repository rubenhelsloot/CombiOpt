package case2;

import java.util.ArrayList;

public class Schedule {
	
	ArrayList<Request> schedule;
	
	Schedule() {
		schedule = new ArrayList<>();
	}
	
	ArrayList<Request> insert(Request s, int i) {
		if (i < schedule.size()) {
			Request r = schedule.get(i);
			schedule.set(i, s);
			return insert(r, i+1);
		} else {
			schedule.add(s);
			return schedule;
		}
	}
}
