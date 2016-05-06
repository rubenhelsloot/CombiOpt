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
    
    public Edge(Edge e) {
        this.start = e.start;
        this.end = e.end;
        length = e.length;
    }
    
    String print() {
    	String line = "(" + start.id;
    	line += !start.isDepot ? " [" + start.r.id + "], " : ", ";
    	line += end.id;
    	line += !end.isDepot ? " [" + end.r.id + "]) " : ") ";
    	return line;
    }
}