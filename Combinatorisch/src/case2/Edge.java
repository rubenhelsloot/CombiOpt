package case2;

public class Edge {
    Location start;
    Location end;
    int length;
    
    public Edge(Location data, Location data2) {
        this.start = data;
        this.end = data2;
        length = data.distance(data2);
    }
    
    void print() {
    	System.out.println(start.id + " " + end.id);
    }
}