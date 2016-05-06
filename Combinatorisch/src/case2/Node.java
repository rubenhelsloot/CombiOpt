package case2;

public class Node {
	Location data;
	Node next;

	Node(Location l, Node n) {
		data = l;
		next = n;
	}

	int size() {
		if (next != null) {
			return next.size() + 1;
		}
		return 1;
	}

	void print() {
		System.out.print(" " + data.id);
		if (next != null) {
			next.print();
		}
	}

	boolean contains(Location l) {
		if (data == l)
			return true;
		if (next != null) {
			return next.contains(l);
		}
		return false;
	}
}