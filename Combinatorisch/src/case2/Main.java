package case2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

	BufferedWriter bw;
	BufferedReader br;
	int days, capacity, maxTripDistance, depotCoordinate, vehicleCost, vehicleDayCost, distanceCost;
	int[][] tools, coordinates, requests, distance;
	Map<String, Integer> map;
	Map<String, int[][]> arrayMap;
	PrintStream out;

	Day[] horizon;
	Depot depot;
	ArrayList<Location> locationList;

	Main() {
		map = new HashMap<>();
		arrayMap = new HashMap<>();
		out = new PrintStream(System.out);
		locationList = new ArrayList<>();
	}

	void printArray(int[][] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				out.printf(array[i][j] + "\t");
			}
			out.printf("\n");
		}
	}

	int maxValue(int[][] array) {
		int result = 0;
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				result = Math.max(result, array[i][j]);
			}
		}
		return result;
	}

	void readFile() throws Exception {
		String line;
		ArrayList<String> al = new ArrayList<>();
		bw.write(br.readLine());
		bw.newLine();
		bw.write(br.readLine());
		bw.newLine();
		bw.newLine();

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

	void setup() {
		horizon = new Day[days];
		depot = new Depot(depotCoordinate, coordinates[depotCoordinate][1], coordinates[depotCoordinate][2], tools,
				capacity, maxTripDistance);
		for (int i = 0; i < days; i++) {
			horizon[i] = new Day(i + 1, depot);
		}

		for (int i = 1; i <= requests.length; i++) {
			Request r = new Request(i, requests, new Tool(requests[i - 1][5], tools));
			Location l = new Location(r, coordinates, depot.location);
			locationList.add(l);
		}
	}

	void schedule() {
		for (int i = 0; i < days; i++) {
			System.out.println("Day " + (i + 1));
			horizon[i].init(locationList);
			horizon[i].scheduleMusts(distance);

			int j = 0;
			while(j < locationList.size()) {
				Location l = locationList.get(j);
				l.endOfDay();
				if (l.r.closed) {
					locationList.remove(l);
				} else {
					j++;
				}
			}
		}
	}
	
	void writeOutput() {
		try {
//			writeSummary();
			writeDay();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void writeSummary() throws Exception {
		int maxVehicles = 0;
		int noVehicleDays = 0;
//		int[] toolUse = new int[horizon.length];
		int distance = 0;
		int cost = 0;
		for (Day d : horizon) {
			maxVehicles = Math.max(maxVehicles, d.tours.size());
			for (Tour t : d.tours) {
				noVehicleDays++;
				distance += t.length();
				cost += vehicleDayCost + distanceCost * t.length();
			}
		}
		cost += vehicleCost * maxVehicles;
	}
	
	void writeDay() throws Exception {
		for (Day d : horizon) {
			if (d.tours.size() > 0) {
				bw.write("DAY = " + d.dayId);
				bw.newLine();
				bw.write("NUMBER_OF_VEHICLES = " + d.tours.size());
				bw.newLine();
				int i = 1;
				String write = "";
				for (Tour t : d.tours) {
					write += i + " R " + depotCoordinate + " ";
					for (Edge e : t.tour) {
						if (!e.end.isDepot) {
							write += e.end.r.printed ? "-" : "";
							e.end.r.printOutput();
							write += e.end.r.id + " ";
						} else {
							write += depotCoordinate;
						}
					}
					i++;
					bw.write(write);
					bw.newLine();
					write = "";
				}
				bw.newLine();
			}
		}
	}

	void start(String input, String output) {
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
			bw = new BufferedWriter(new FileWriter(output));
			readFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

		setup();
		schedule();
		
		writeOutput();
	}

	public static void main(String[] args) {
		new Main().start(args[0], args[1]);
	}
}
