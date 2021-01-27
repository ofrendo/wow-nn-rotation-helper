package de.nnrh.generate;

import java.io.IOException;

import de.nnrh.Config;

public class GenerateLogs {
	
	public static int generateFightLength() {
		// Vary fight length between 240s and 360s
		double fightLengthBase = 300;
		double diffInterval = 120;
		
		// generate number between -0.5 and 0.5
		double r = Math.random() - 0.5;
		double result = fightLengthBase + diffInterval*r;
		return (int) result;
	}
	
	public static void generateLogs() throws IOException {
		
		System.out.println("Generating logs...");
		
		for (int i=0;i<Config.LOGS_AMOUNT;i++) {
			
			String logFile = Config.pathDirLogs + Config.baseLogName + i + Config.baseLogEnding;
			String command = Config.pathSimc + " \"" + Config.pathSimcProfile + "\" "
					+ "output=\"" + logFile + "\" max_time=" + generateFightLength();
			
			//System.out.println("Executing command:");
			//System.out.println(command);
			
			Runtime.getRuntime().exec(command);
			
			if (i%10 == 0){
				System.out.println("Generated " + i + " logs.");
			}
		}
		
		System.out.println("Finished generating logs.");
		
	}
	
	public static void main(String[] args) throws IOException {
		generateLogs();
	}

}
