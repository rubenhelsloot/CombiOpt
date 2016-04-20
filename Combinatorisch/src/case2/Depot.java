package case2;

import java.util.ArrayList;

public class Depot {
	Location location;
	int maxCap;
	int maxDist;
	ArrayList<Tool> toolstack;
	ArrayList<Vehicle> carpark;
	
	Depot(int id, int x, int y, int[][] tools, int maxCap, int maxDist) {
		location = new Location(id, x, y);
		this.maxCap = maxCap;
		this.maxDist = maxDist;
		toolstack = new ArrayList<>();
		carpark = new ArrayList<>();
		init(tools);
	}
	
	void init(int[][] tools) {
		for(int i = 0; i < tools.length; i++) {
			for(int j = 0; j < tools[i][2]; j++) {
				toolstack.add(new Tool(tools[i][0], tools));
			}
		}
	}
	
	int toolsByType(int type) {
		int result = 0;
		for(Tool t : toolstack) {
			if(t.type == type) result ++;
		}
		return result;
	}
	
	void planVehicleRoute(Request[] planning) {
		if (carpark.size() == 0) carpark.add(new Vehicle(maxCap, maxDist));
		Vehicle v = carpark.get(0);
		v.plan(planning);
	}
}
