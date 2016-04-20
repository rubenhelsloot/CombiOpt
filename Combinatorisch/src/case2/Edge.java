package case2;

public class Edge {
    Location start;
    Location end;
    int length;
    public Edge(Location start, Location end) {
        start = start;
        end = end;
        length = start.distance(end);
    }
}