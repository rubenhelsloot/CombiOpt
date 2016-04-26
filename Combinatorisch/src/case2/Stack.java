package case2;

public class Stack {
    Node header;

    public Stack() {
        header = null;
    }

    public void push(Location l) {
        header = new Node(l, header);
    }
    public Location pop() {
        Location popped = header.data;
        header = header.next;
        return popped;
    }
    public Location peek(){
        return header.data;
    }
    
    public int size() {
    	return header.size();
    }
    
    public void print() {
    	System.out.print("Stack:");
    	header.print();
    	System.out.print("\n");
    }
}