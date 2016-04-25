package case2;

public class Node {
    Request data;
    Node next;
    public Node(Request d, Node n) {
        data = d;
        next = n;
    }
    
    public int size() {
    	if(next != null) {
    		return next.size() + 1;
    	}
    	return 1;
    }
    
    public void print() {
    	System.out.println("Data id: " + data.id);
    	if (next != null) {
    		next.print();
    	}
    }
}