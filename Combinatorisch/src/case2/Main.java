package case2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

	BufferedReader br = null;
	FileReader fr;
	int days, capacity, maxTripDistance, depotCoordinate, vehicleCost, vehicleDayCost, distanceCost;
	int[][] tools, coordinates, requests, distance;
	Map<String, Integer> map;
	Map<String, int[][]> arrayMap;
	PrintStream out;

	Main() {
		map = new HashMap<>();
		arrayMap = new HashMap<>();
		out = new PrintStream(System.out);
	}

	Main(String str) {
		String[] s = { str };
		map = new HashMap<>();
		arrayMap = new HashMap<>();
		out = new PrintStream(System.out);
		start(s);
	}

	void readFile(BufferedReader br) throws Exception {
		String line;
		ArrayList<String> al = new ArrayList<>();
		br.readLine();
		br.readLine();
		
		while ((line = br.readLine()) != null) {
			if (line.length() == 0) {
				continue;
			}
			al.add(line);
		}
		
		readVariables(al);
		
		days = map.get("DAYS");
		capacity = map.get("CAPACITY");
		maxTripDistance = map.get("MAX_TRIP_DISTANCE");
		depotCoordinate = map.get("DEPOT_COORDINATE");
		vehicleCost = map.get("VEHICLE_COST");
		vehicleDayCost = map.get("VEHICLE_DAY_COST");
		distanceCost = map.get("DISTANCE_COST");
		tools = arrayMap.get("TOOLS");
		coordinates = arrayMap.get("COORDINATES");
		requests = arrayMap.get("REQUESTS");
		distance = arrayMap.get("DISTANCE");
	}

	void readVariables(ArrayList<String> al) {
		while (al.get(0).charAt(0) != 'T') {
			Scanner s = new Scanner(al.get(0));
			s.useDelimiter(" ");
			String varName = s.next();
			s.next();
			map.put(varName, s.nextInt());
			al.remove(0);
			s.close();
		}

		al = readArray(readArray(readArray(al)));

		if (al.size() > 0 && al.get(0).equals("DISTANCE")) {
			al.remove(0);
			readDistance(al);
		} else {
			calculateDistance();
		}
	}

	ArrayList<String> readArray(ArrayList<String> al) {
		String str = al.get(0);
		al.remove(0);
		Scanner s = new Scanner(str);
		s.useDelimiter(" ");

		String varName = s.next();
		s.next();
		int height = s.nextInt();
		int width = varName.equals("TOOLS") ? 4 : varName.equals("COORDINATES") ? 3 : 7;
		int[][] array = new int[height][width];
		s.close();

		for (int i = 0; i < height; i++) {
			Scanner line = new Scanner(al.get(0));
			line.useDelimiter("\t");
			al.remove(0);

			for (int j = 0; j < width; j++) {
				array[i][j] = line.nextInt();
			}
			line.close();
		}
		arrayMap.put(varName, array);
		return al;
	}

	void readDistance(ArrayList<String> al) {
		int size = al.size();
		int[][] array = new int[size][size];

		for (int i = 0; i < size; i++) {
			Scanner s = new Scanner(al.get(0));
			al.remove(0);
			s.useDelimiter("\t");
			for (int j = 0; j < size; j++) {
				array[i][j] = s.nextInt();
			}
			s.close();
		}

		arrayMap.put("DISTANCE", array);
	}

	void calculateDistance() {
		coordinates = arrayMap.get("COORDINATES");
		int size = coordinates.length;
		distance = new int[size][size];
		
		for (int i = 0; i < size; i++) {
			distance[i][i] = 0;
			for (int j = i + 1; j < size; j++) {
				double dx = Math.pow(coordinates[i][1] - coordinates[j][1], 2.0);
				double dy = Math.pow(coordinates[i][2] - coordinates[j][2], 2.0);
				int value = (int) Math.floor(Math.sqrt(dx + dy));
				distance[i][j] = distance[j][i] = value;
			}
		}
		arrayMap.put("DISTANCE", distance);
	}
	
	void printArray(int[][] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				out.printf(array[i][j] + "\t");
			}
			out.printf("\n");
		}
	}

	void start(String[] args) {
		for (int i = 0; i < args.length; i++) {
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(args[i])));
				try {
					readFile(br);
				} finally {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new Main().start(args);
	}
}

//TODO: Construct vehicle class
//TODO: Construct depot class
//TODO: Construct location class
