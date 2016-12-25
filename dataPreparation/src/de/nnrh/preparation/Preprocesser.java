package de.nnrh.preparation;

public class Preprocesser {
	
	String pathLogs = "../data/logs/";
	String path = pathLogs + "log.txt";
	String pathCleanLog = pathLogs + "logClean.txt";
	
	String pathCSV = "../data/csv/";
	
	/**
	 * Go backwards
	 * @param content
	 * @return
	 */
	public String checkActivePassiveSpells(String content) {
		String result = "";
		String[] lines = content.split("\r\n");
		for (int i=lines.length-1; i>-1; i--) {
			String line = lines[i];
			// Check if this is a "performs" line
			if (line.matches("[1-9]\\d*\\.\\d{3}\\s\\w+\\s(performs)\\s.*")) {
				String spellname = line.split(" ")[3];
				// Now check previous lines for execute
				for (int j=i;j>-1;j--) {
					String compareLine = lines[j];
					if (compareLine.matches(LineMatcher.regexStart + 
							"(schedules\\sexecute\\sfor\\s" + spellname + ")")) {
						result = line + "\n" + result;
						//System.out.println("Found execute line for " + spellname);
						//System.out.println(compareLine);
						break;
					}
				}
				//if (!found)
				//	System.out.println("Not adding " + spellname);
			}
			// Add all lines except those matching "...schedules execute"
			else if (!line.matches(LineMatcher.regexStart + 
					"(schedules\\sexecute\\sfor\\s).*") || 
					line.contains("use_item_")) {
				result = line + "\n" + result;
			}
		}
		
		return result;		
	}
	
	/**
	 * @param inputPath Path to input .log
	 * @param outputPath Path to output .csv
	 */
	public void processFile(String inputPath, String outputPath) {
		String content = FileManager.getInstance().readFile(inputPath);
		
		// Do additional preprocessing to only keep ACTIVE spells
		content = checkActivePassiveSpells(content);
		//System.out.println(content);
		
		FileManager.getInstance().writeFile(outputPath, content);
	}
	
	public static void main(String[] args) {
		Preprocesser p = new Preprocesser();
		p.processFile(p.path, p.pathCleanLog);
		
	}

}
