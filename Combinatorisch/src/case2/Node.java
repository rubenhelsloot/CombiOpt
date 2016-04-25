package case2;

public class Node {
    Location data;
    Node next;
    public Node(Location d, Node n) {
        data = d;
        next = n;
    }
    
    public int size() {
    	if(next != null) {
    		return next.size() + 1;
    	}
    	return 1;
    }
}