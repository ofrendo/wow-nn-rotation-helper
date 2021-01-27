package de.nnrh.preparation;

import de.nnrh.Config;
import de.nnrh.preparation.CSVConverter.CSV;

public class Preprocesser {
	
	CSVConverter csvConverter = null;
	
	public Preprocesser() {
		csvConverter = new CSVConverter();
	}
	
	/**
	 * Go backwards
	 * @param content
	 * @return
	 */
	public String checkActivePassiveSpells(String content) {
		String result = "";
		String[] lines = content.split(System.lineSeparator());
		for (int i=lines.length-1; i>-1; i--) {
			String line = lines[i];
			// Check if this is a "performs" line that is not at start of combat
			if (line.matches("[1-9]\\d*\\.\\d{3}\\s\\w+\\s(performs)\\s.*")) {
				String spellname = line.split(" ")[3];
				// Now check previous lines for execute
				for (int j=i;j>-1;j--) {
					String compareLine = lines[j];
					if (compareLine.matches(LineMatcher.regexStart + 
							"(schedules\\sexecute\\sfor\\s" + spellname + ")")) {
						result = line + System.lineSeparator() + result;
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
				result = line + System.lineSeparator() + result;
			}
		}
		
		return result;		
	}
	
	
	
	/**
	 * @param inputPath Path to input .log
	 * @param outputPath Path to output .csv
	 */
	public void processFile(int i) {
		// Read only some lines
		String pathInputFile = Config.pathDirLogs + Config.baseLogName + i + Config.baseLogEnding;
		String pathInputFileCleaned = Config.pathDirLogs + Config.baseLogCleanedName + i + Config.baseLogEnding;
		String outputPath = Config.pathDirCSV + Config.baseCSVName + i + Config.baseCSVEnding;
		
		String cleanedLog = FileManager.getInstance().readFile(pathInputFile);
		
		// Do additional preprocessing to only keep ACTIVE spells
		cleanedLog = checkActivePassiveSpells(cleanedLog);
		FileManager.getInstance().writeFile(pathInputFileCleaned, cleanedLog);
		
		// Transform cleanedLog to csv
		CSV csv = csvConverter.transformToCSV(cleanedLog);		
		
		// Write content and metadata (labels) to file
		//FileManager.getInstance().writeFile(Config.pathLabels, csv.actions);
		FileManager.getInstance().writeFile(outputPath, csv.content);
	}
	
	public void processAllFiles() {
		for (int i=0;i<Config.LOGS_AMOUNT;i++) {
			if (i%10==0) {
				System.out.println("Processing file " + i + "/" + Config.LOGS_AMOUNT + "...");
			}
			
			processFile(i);
		}
	}
	
	public void combineFiles() {
		System.out.println("Combining files...");
		String contentAll = "";
		for (int i=0;i<Config.LOGS_AMOUNT;i++) {
			
		}
				
		FileManager.getInstance().writeFile(Config.pathCSVCombined, contentAll);
		System.out.println("Combined files.");
	}
	
	public static void main(String[] args) {
		Preprocesser p = new Preprocesser();
		//p.processFile(1);
		p.processAllFiles();
		
		
	}

}
