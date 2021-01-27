package de.nnrh.preparation;

import java.util.ArrayList;
import java.util.HashMap;

public class CSVConverter {
	

	private LineType getLineType(String line) {
		LineType result = null;
		for (LineType l : LineType.values()) {
			if (line.matches(l.regex)) {
				result = l;
				break;
			}
		}
		return result;
	}
	
	class CSV {
		public CSV(ArrayList<String> actionsList, String content) {
			this.content = content;
			for (int i=0;i<actionsList.size();i++) {
				actions += actionsList.get(i);
				if (i!=actionsList.size()-1) {
					actions += Util.CSV_DELIMITER;
				}
			}
		}
		String content;
		String actions;
	}
	
	public CSV transformToCSV(String logLines) {
		String[] linesString = logLines.split("\r\n");

		// Records how many times a spell was cast
		HashMap<String, Integer> spells = new HashMap<>();
		HashMap<String, Integer> items = new HashMap<>();
		
		// Records whether a buff is active with how many stacks:
		// gains xyz_1 ==> buff is set to 1
		// loses xyz_1 ==> buff is set to 0
		HashMap<String, Integer> buffs = new HashMap<>();
		
		// Records whether a debuff is active with how many stacks
		HashMap<String, Integer> debuffs = new HashMap<>();
		
		ArrayList<String> actions = new ArrayList<>();
		ArrayList<HashMap<String,Integer>> lines = new ArrayList<>();
		
		// Pass through all lines first without materializing CSV
		for (int i=0;i < linesString.length;i++) {
			
			String line = linesString[i];
			LineType lineType = getLineType(line);
			if (lineType == null) {
				// Skip this line, for example for empty lines
				System.out.println("Ignoring line for csv output (" + line + ")");
				continue; 
			}
			
			String name = line.split(" ")[3];
			
			// If a buff/debuff was not in the list upon a "loses" message, ignore it
			HashMap<String, Integer> lineHash = new HashMap<>();
			int ts = Integer.parseInt(line.split(" ")[0].replaceAll("\\.", ""));
			lineHash.put("TS", ts);
			switch(lineType) {
				case SPELL: 
					name = "S_" + name;
					
					// Create entry of spell itself
					spells.put(name,0);
					lineHash.put(name, 1);
					
					break;
				case ITEM:
					name = line.split(" ")[5]; // line looks different, here it's the 5th word
					name = name.replaceAll("use_item_", "");
					name = "I_" + name;
					
					//Create entry of item itself
					items.put(name,0);
					lineHash.put(name,  1);
					break;
				case BUFF_GAIN: 
					int stackAmount = Util.getStackAmount(name);
					name = name.replaceAll("_\\d+", "");
					name = "B_" + name;
					buffs.put(name, stackAmount);
					break;
				case BUFF_LOSS: 
					name = "B_" + name;
					// Ignore it if not previously gained
					if (buffs.get(name) != null) {
						buffs.put(name, 0);
					}
					else {
						continue;
					}
					break;
				case DEBUFF_GAIN: 
					stackAmount = Util.getStackAmount(name);
					name = name.replaceAll("_\\d+", "");
					name = "D_" + name;
					debuffs.put(name, stackAmount); 
					break; 
				case DEBUFF_LOSS: 
					name = "D_" + name;
					if (debuffs.get(name) != null) {
						debuffs.put(name, 0);
					}
					else {
						continue;
					}
					break;
			}
			
			// Add it to header line if it doesn't exist
			//if (names.contains(name) == false) {
			//	names.add(name);
			//}
			
			// Create entries for all buffs and debuffs
			for (String buff : buffs.keySet()) {
				int stackAmount = buffs.get(buff); // how many stacks does the buff have currently
				if (stackAmount > 0) {
					lineHash.put(buff, stackAmount);
				}
			}
			for (String debuff : debuffs.keySet()) {
				int stackAmount = debuffs.get(debuff);
				if (stackAmount > 0) {
					lineHash.put(debuff, stackAmount);
				}
			}	
			
			// peek ahead to get the label: next spell perform or item use
			for (int j=i+1;j<linesString.length;j++) {
				String compareLine = linesString[j];
				String labelName = null;
				if (compareLine.matches(LineType.SPELL.regex)) {
					// workaround because we're using <String,Int> hashmaps
					labelName = compareLine.split(" ")[3];
				}
				else if (compareLine.matches(LineType.ITEM.regex)) {
					labelName = compareLine.split(" ")[5];
				}
				
				if (labelName != null) {
					if (!actions.contains(labelName)) {
						actions.add(labelName);
						lineHash.put("LABEL", actions.size()-1);
					}
					else {
						lineHash.put("LABEL", actions.indexOf(labelName));
					}
					break;
				}
			}
			if (lineHash.get("LABEL") == null)
				lineHash.put("LABEL", -1);
			
			// Add finished lineHash to lines
			lines.add(lineHash);
			
		}
		
		ArrayList<String> names = getNames(spells, items, buffs, debuffs);
		String result = materializeCSV(names, actions, lines);
		return new CSV(actions, result);		
	}

	public ArrayList<String> getNames(HashMap<String,Integer> spells,
			HashMap<String,Integer> items,
			HashMap<String,Integer> buffs,
			HashMap<String,Integer> debuffs) {
		ArrayList<String> result = new ArrayList<String>();
		result.add("TS");
		result.addAll(spells.keySet());
		result.addAll(items.keySet());
		result.addAll(buffs.keySet());
		result.addAll(debuffs.keySet());
		result.add("LABEL");
		return result;
	}
	
	public String materializeCSV(ArrayList<String> names, 
			ArrayList<String> actions, 
			ArrayList<HashMap<String,Integer>> lines) {
		// Write first line
		String result = buildCSVHeader(names) + System.lineSeparator();
		
		// Write content by materializing line hashes
		for (int i=0;i<lines.size();i++) {
			result += materializeLineHash(names, actions, lines.get(i)) + System.lineSeparator();
		}
		return result;
	}
	
	public String materializeLineHash(ArrayList<String> names,
			ArrayList<String> actions,
			HashMap<String,Integer> lineHash) {
		String result = "";
		for (int i=0;i<names.size();i++) {
			
			String name = names.get(i);
			
			if (name.equals("LABEL")) {
				Integer index = lineHash.get(name);
				result += (index >= 0) ? 
						actions.get(index) : 
						"NULL";
			}
			else {
				Integer number = lineHash.get(name);
				if (number != null) {
					result += number;
				}
				else {
					result += "0";
				}
			}
			
			if (i!=names.size()-1) 
				result += Util.CSV_DELIMITER;
		}
		
		return result;
	}
	
	/*public String hashMapToCSVHeader(HashMap<String,Integer> input) {
		String result = "";
		for (String key : input.keySet()) {
			result += key + Util.CSV_DELIMITER;
		}
		return result;
	}*/
	
	public String buildCSVHeader(ArrayList<String> names) {
		String result = "";
		for (int i=0;i<names.size();i++) {
			result += names.get(i);
			
			if (i!=names.size()-1) {
				result += Util.CSV_DELIMITER;
			}
		}
		
		return result;
	}
	
	
}
