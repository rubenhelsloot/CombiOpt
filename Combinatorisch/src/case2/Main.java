package case2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
	
	BufferedReader br = null;
	FileReader fr;
	public int DAYS, CAPACITY, MAX_TRIP_DISTANCE, DEPOT_COORDINATE, VEHICLE_COST,
			VEHICLE_DAY_COST, DISTANCE_COST;
	int[][] tools, coordinates, requests;
	Map<String, Integer> map;
	Map<String, int[][]> arrayMap;

	Main() {
		map = new HashMap<>();
		arrayMap = new HashMap<>();
	}
	
	void readFile(BufferedReader br) throws Exception {
		String line;
		ArrayList<String> al = new ArrayList<>();
		br.readLine();
		br.readLine();
	    while ((line = br.readLine()) != null) {
	        if(line.length() == 0) {
	        	continue;
	        }
	        al.add(line);
	    }
	    readAll(al);
	}
	
	void readAll(ArrayList<String> array) {
		while(array.get(0).charAt(0) != 'T') {
			Scanner s = new Scanner(array.get(0));
			s.useDelimiter(" ");
			String varName = s.next();
			s.next();
			map.put(varName, s.nextInt());
			array.remove(0);
			s.close();
		}
		
		array = readArray(readArray(readArray(array)));
		
//		if (array.size() > 0 && array.get(0).equals("DISTANCE")) {
//			array.remove(0);
//			readDistance(array);
//		}
	}
	
	ArrayList<String> readArray(ArrayList<String> array) {
		String str = array.get(0);
		array.remove(0);
		Scanner s = new Scanner(str);
		s.useDelimiter(" ");
		
		String varName = s.next();
		s.next();
		int height = s.nextInt();
		int width = varName.equals("TOOLS") ? 4 : varName.equals("COORDINATES") ? 3 : 7;
		int[][] arr = new int[height][width];
		
		for (int i = 0; i < height; i++) {
			Scanner line = new Scanner(array.get(0));
			line.useDelimiter("\t");
			array.remove(0);
			
			for(int j = 0; j < width; j++) {
				arr[i][j] = line.nextInt();
			}
			line.close();
		}
		arrayMap.put(varName, arr);
		return array;
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
