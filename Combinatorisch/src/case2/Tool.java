package case2;

public class Tool {

	int type;
	long size;
	long globalAvailable;
	long cost;
	boolean onLocation;
	boolean inUse;
	
	Tool(int type, long[][] array) {
		this.type = type;
		size = array[type - 1][1];
		globalAvailable = array[type - 1][2];
		cost = array[type - 1][3];
		onLocation = false;
		inUse = false;
	}
	
	Tool(long type, long[][] array) {
		this.type = (int) type;
		size = array[this.type - 1][1];
		globalAvailable = array[this.type - 1][2];
		cost = array[this.type - 1][3];
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
