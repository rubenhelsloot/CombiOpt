package case2;

public class Node {
    Location data;
    Node next;
    public Node(Location l, Node n) {
        data = l;
        next = n;
    }
    
    public int size() {
    	if(next != null) {
    		return next.size() + 1;
    	}
    	return 1;
    }
    
    public void print() {
    	System.out.print(" " + data.id);
    	if (next != null) {
    		next.print();
    	}
    }
}