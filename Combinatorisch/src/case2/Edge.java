package case2;

public class Edge {
    Location start;
    Location end;
    int length;
    public Edge(Location start, Location end) {
        this.start = start;
        this.end = end;
        length = start.distance(end);
    }
}