package case2;

public class Request {

	public int id;
	public int locationId;
	public int first;
	public int last;
	public int duration;
	public int type;
	public int amount;
	public boolean delivered;
	public Tool[] stack;
	public int remaining;

	Request(int id, int[][] array) {
		this.id = id;
		locationId = array[id - 1][1];
		first = array[id - 1][2];
		last = array[id - 1][3];
		duration = array[id - 1][4];
		type = array[id - 1][5];
		amount = array[id - 1][6];
		delivered = false;
		stack = new Tool[amount];
	}

	void isDelivered() {
		delivered = true;
	}

	void endOfDay() {
		if (delivered)
			duration--;

		for (Tool t : stack) {
			t.endOfDay();
		}
	}

	void clearStack() {
		stack = new Tool[0];
	}
}
