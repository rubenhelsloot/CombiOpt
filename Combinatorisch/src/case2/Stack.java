package case2;

public class Stack {
    Node header;

    Stack() {
        header = null;
    }

    void push(Location l) {
        header = new Node(l, header);
    }
    Location pop() {
        Location popped = header.data;
        header = header.next;
        return popped;
    }
    Location peek(){
        return header.data;
    }
    
    int size() {
    	return header.size();
    }
    
    void print() {
    	System.out.print("Stack:");
    	header.print();
    	System.out.print("\n");
    }
    
    boolean contains (Location l) {
    	return header.contains(l);
    }
}