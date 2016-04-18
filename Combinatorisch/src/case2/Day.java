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
	
	void scheduleMusts(int carpool) {
		for (Request r : musts) {
			System.out.println(r.id);
		}
	}
}