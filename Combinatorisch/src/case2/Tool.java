package case2;

public class Tool {

	int type;
	int size;
	int globalAvailable;
	int cost;
	boolean onLocation;
	boolean inUse;
	
	Tool(int type, int[][] array) {
		this.type = type;
		size = array[type - 1][1];
		globalAvailable = array[type - 1][2];
		cost = array[type - 1][3];
		onLocation = false;
		inUse = false;
	}
	
	void endOfDay() {
		inUse = onLocation;
	}
	
	void isMoved(int daysTillPickup) {
		onLocation = !onLocation; 
		inUse = onLocation;
	}
}
