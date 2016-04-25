package case2;

public class Edge {
    Request start;
    Request end;
    int length;
    public Edge(Request data, Request data2) {
        this.start = data;
        this.end = data2;
        length = data.location.distance(data2.location);
    }
}