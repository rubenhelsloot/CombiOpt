package case2;

import java.util.ArrayList;
import java.util.Collections;

public class KMeans {

	int k;
	ArrayList<Location> points;
	ArrayList<Cluster> clusters;

	KMeans(int k, ArrayList<Location> l, Location depot) {
		this.k = k;
		points = l;
		points.remove(depot);
		clusters = new ArrayList<>(k);

		init();
		calculate();
	}

	void init() {
		System.out.print("Starting " + k + "-means with ");
		for(Location l : points) 
			System.out.print(l.id + " ");
		System.out.print("\n");
		
		int[] values = uniqueRandomValues();
		for (int i = 0; i < k; i++) {
			Cluster cluster = new Cluster(i);
			Location centroid = new Location(points.get(values[i]).x, points.get(values[i]).y);
			cluster.centroid = centroid;
			clusters.add(cluster);
		}

		print(); // Debug
	}

	void calculate() {
		boolean finish = false;
		int iteration = 0;

		while (!finish) {
			clearClusters();
			ArrayList<Location> oldCentroids = centroids();
			assignCluster();
			calculateCentroids();
			iteration++;

			ArrayList<Location> newCentroids = centroids();
			
			int distance = 0;
			for (int i = 0; i < oldCentroids.size(); i++) {
				distance += oldCentroids.get(i).distance(newCentroids.get(i));
			}
			System.out.println("Iteration: " + iteration);
			System.out.println("Centroid distances: " + distance);
			print();

			if (distance == 0) {
				finish = true;
			}
		}
	}

	void calculateCentroids() {
		for (int i = 0; i < k; i++) {
			Cluster cluster = clusters.get(i);
			double sumx = 0;
			double sumy = 0;
			int size = cluster.points.size();

			for (Location l : cluster.points) {
				sumx += l.x;
				sumy += l.y;
			}

			if (size > 0) {
				double newx = sumx / size;
				double newy = sumy / size;
				cluster.centroid.x = (int) newx;
				cluster.centroid.y = (int) newy;
			}
		}
	}

	void assignCluster() {
		double max = Double.MAX_VALUE;
		double min = max;
		int cluster = 0;
		double distance = 0.0;

		for (Location l : points) {
			min = max;
			for (int i = 0; i < k; i++) {
				Cluster c = clusters.get(i);
				distance = l.distance(c.centroid);
				if (distance < min) {
					min = distance;
					cluster = i;
				}
			}
			l.setCluster(cluster);
			clusters.get(cluster).points.add(l);
		}
	}

	void clearClusters() {
		for (Cluster cluster : clusters) { // Reset clusters
			cluster.clear();
		}
	}
	
	ArrayList<Location> centroids() {
		ArrayList<Location> result = new ArrayList<>();
		for (Cluster c : clusters) {
			result.add(new Location(c.centroid.x, c.centroid.y));
		}
		return result;
	}

	void print() {
		for (Cluster c : clusters) {
			c.print();
		}
	}
	
	ArrayList<ArrayList<Location>> getClusters() {
		ArrayList<ArrayList<Location>> result = new ArrayList<>();
		for (Cluster c : clusters) {
			result.add(c.points);
		}
		return result;
	}

	int[] uniqueRandomValues() {
		int[] result = new int[k];
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < points.size(); i++)
			list.add(i);
		Collections.shuffle(list);
		for (int i = 0; i < k; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	private class Cluster {

		public ArrayList<Location> points;
		public Location centroid;
		public int id;

		public Cluster(int id) {
			this.id = id;
			points = new ArrayList<>();
			centroid = null;
		}

		public void print() {
			System.out.println("[Cluster: " + id + " ]");
			System.out.println("[Centroid: " + centroid.x + ", " + centroid.y + " ]");
			System.out.print("[Points: ");
			for (Location p : points) {
				System.out.print(p.id + " ");
			}
			System.out.print("]\n");
		}
		
		void clear() {
			points.clear();
		}
	}
}
