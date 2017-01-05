package de.nnrh;

public class Config {
	
	// Log generation
	public static int LOGS_AMOUNT = 100;
	public static String pathSimc = 
			"C:\\Users\\D059373\\workspace git\\wow-nn-rotation-helper\\"
			+ "data\\simc\\simc-715-01-win64-cec0ee9\\simc-715-01-win64\\simc.exe";
	public static String pathSimcProfile = 
			"C:\\Users\\D059373\\workspace git\\wow-nn-rotation-helper\\"
			+ "data\\simc\\zhadokmage.simc";
	
	public static String pathDirLogs = 
			"C:\\Users\\D059373\\workspace git\\wow-nn-rotation-helper\\data\\logs\\";
	public static String baseLogName = "log";
	public static String baseLogCleanedName = "logCleaned";
	public static String baseLogEnding = ".txt";

	// Preprocessing
	public static String pathDirCSV = 
			"C:\\Users\\D059373\\workspace git\\wow-nn-rotation-helper\\data\\csv\\";
	public static String baseCSVName = "log";
	public static String baseCSVEnding = ".csv";
	
	public static String pathLabels = pathDirCSV + "labelActionMap.txt";
	public static String pathCSVCombined = pathDirCSV + "logAll" + baseCSVEnding;
	
	
}
