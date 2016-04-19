package case2;

import java.util.ArrayList;

public class Day {
	
	public ArrayList<Request> musts;
	public ArrayList<Request> mays;
	public int dayId;
	
	Day(int dayId) {
		this.dayId = dayId;
		musts = new ArrayList<>();
		mays = new ArrayList<>();
	}
	
	void init(ArrayList<Request> requests) {
		classifyTasks(requests);
	}
	
	void classifyTasks(ArrayList<Request> requests) {
		//Need pickup or is last day of time window
		for(Request r : requests) {
			if ((r.delivered && r.remaining == 0) || (!r.delivered && r.last == dayId)) {
				musts.add(r);
			} else if (!r.delivered && dayId >= r.first) {
				mays.add(r);
			}
		}
	}
	
	int getInitialToolSpace(int[][] tools) {
		int result = 0;
		for (Request r : musts) {
			if (r.delivered) {
				result -= r.stack[0].size * r.amount;
			} else {
				result += new Tool(r.type, tools).size * r.amount;
			}
		}
		return result;
	}
	
	void scheduleMusts(int carpool, int maxDistance) {
		ArrayList<Request> pickups = new ArrayList<>();
		ArrayList<Request> deliveries = new ArrayList<>();
		for (Request r : musts) {
			if (r.delivered) { pickups.add(r); }
			else { deliveries.add(r); }
		}
		
		recursiveSchedule(pickups, deliveries);
	}
	
	void recursiveSchedule(ArrayList<Request> pickups, ArrayList<Request> deliveries) {
//		if(pickups.size() > 0) {
//			Request r = pickups.get(0);
//			deliveries = sortByLocation(deliveries, r.location);
//			
//		}
	}
	
	ArrayList<Request> sortByLocation(ArrayList<Request> requests, Location l) {
		if (requests.size() < 2) return requests;
		
		for(int i = 0; i < requests.size() - 1; i++) {
			for(int j = 0; j < requests.size() - i - 1; j++) {
				if (requests.get(j).location.distance(l) > requests.get(j+1).location.distance(l)) {
					Request r = requests.get(j);
					requests.set(j, requests.get(j+1));
					requests.set(j+1, r);
				}
			}
		}
		
		return requests;
	}
}