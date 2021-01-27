package de.nnrh.preparation;

import java.util.HashMap;

public class Util {

	public static final String CSV_DELIMITER = ";";
	
	public static String getFilename(String inputPath) {
		String[] parts = inputPath.split("/");
		String filename = parts[ parts.length-1 ];
		return filename.split("\\.")[0];
	}
	
	public static int getStackAmount(String buff) {
		// Buff looks like pyretic_incantation_2
		String[] parts = buff.split("_");
		String lastPart = parts[parts.length-1];
		return Integer.parseInt(lastPart);
		/*if (lastPart.matches("\\d+")) {
		}
		else {
			return 0;
		}*/
		
	}
	
	public static void main(String[] args) {
		
		String test = "deadly_potion_134";
		System.out.println(test.replaceAll("_\\d+", ""));
		
		System.out.println(test.substring(0, test.length()-1));
		
		System.out.println(getFilename("logs/log.txt"));
		
		HashMap<String,Integer> testMap = new HashMap<>();
		System.out.println(testMap.get("doesntexist"));
	}
	
}
