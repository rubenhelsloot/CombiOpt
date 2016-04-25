package case2;

public class Stack {
    Node header;

    public Stack() {
        header = null;
    }

    public void push(Request r) {
        header = new Node(r, header);
    }
    public Request pop() {
        Request popped = header.data;
        header = header.next;
        return popped;
    }
    public Request peak(){
        return header.data;
    }
    
    public int size() {
    	return header.size();
    }
    
    public void print() {
    	header.print();
    }
}