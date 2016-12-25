package de.nnrh.preparation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Should read and write files
 */
public class FileManager {
	
	private static FileManager instance;
	
	private FileManager() { 
		
	}
	
	public static FileManager getInstance() {
		if (instance == null) {
			instance = new FileManager();
		}
		return instance;
	}
	
	public String readFile(String path) {
		String result = "";
		
		LineMatcher lineMatcher = new LineMatcher();
		BufferedReader br = null;
		int lineNumber = 0;
		try {
			br = new BufferedReader(new FileReader(path));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        
	        int linesToSkip = 0;
	        while (line != null) {
	        	lineNumber++;
	        	if (lineNumber % 100 == 0) {
	        		//System.out.println(line);
	        		//System.out.println(lineNumber);
	        	}
	        	
	        	String lineCopy = line;
	        	line = br.readLine();
	        	
	        	// In case any lines should be skipped from previous iteration
	        	if (linesToSkip > 0) {
	        		linesToSkip--;
	        		continue;
	        	}
	        	
	        	linesToSkip = lineMatcher.lineMatches(lineCopy);
	        	if (linesToSkip > 0) {
	        		linesToSkip--;
	        		continue;
	        	}
	            sb.append(lineCopy);
	            sb.append(System.lineSeparator());
	            
	        }
	        result = sb.toString();
	    } catch (IOException e) { 
	    	e.printStackTrace();
	    } 
		finally {
	        try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		
		return result;
	}
	
	public void writeFile(String path, String content) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(path);
			out.print(content);
			out.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			out.close();
		}
		
	}
	
	
}
