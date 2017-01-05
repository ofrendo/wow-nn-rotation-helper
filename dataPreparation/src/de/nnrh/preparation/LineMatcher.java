package de.nnrh.preparation;

/**
 * Use regex to remove all lines that are irrelevant
 */
public class LineMatcher {
	
	// Matches 0.000 NAME 
	public static String regexStart = "\\d+\\.\\d{3}\\s\\w+\\s";
	
	// Allowed verbs: Words that occur after NAME
	/*private String[] allowedVerbs = {
			"performs",
			"gains"
	};*/
	// Any verbs that are not allowed
	private String[] disallowedVerbs = {
			"tries",
			"arises",
			"food",
			"augmentation",
			"potion",
			"consumes",
			//"schedules, // probably need this to see which spells are passive and which are active
			"schedules\\stravel",
			"demises"
	};
	
	// Any spells that are not allowed that occur after "performs"
	private String[] disallowedSpells = {
			"snapshot_stats",
			"deadly_grace", // Is "performed" upon a proc
			"mark_of_the_hidden_satyr", // Is "performed" upon a proc
			"spread", // example:  Zhadokmage ignite spread event occurs
			"ticks" // example: Zhadokmage ignite ticks (1 of 9) Fluffy_Pillow for 8279 fire damage (hit)
			
			// Specific stuff...
			, "maddening_whispers" // this gets "performed" after 10 stacks
	};
	
	// Any buffs that are not allowed that occur after "gains"
	private String[] disallowedBuffs = {
 			"\\d+\\.\\d{2}", // any numbers like "1300.00 intellect" or mana gains
			"casting(_)?\\d*",
			"Health",
			"mortal_wounds_1",
			"bleeding_1",
			"flask_\\w+",
			"defiled_augmentation",
			//"potion_of_\\w+\\d" // "Performs potion" should be kept, not "gains potion_of_x"
			// Food and flask should be caught by above disallowedSpells
	};
	
	// In simcraft logs these take up current line and next 2 lines
	private String[] disallowed3LineSpells = {
			"flask",
			"food",
			"augmentation"
	};
	
	public String regex = "";
	private String regex3Lines = "";
	
	public LineMatcher() {
		//regex = regexStart;
		regex = "(" + regexStart + "(";
		for (int i=0;i<disallowedVerbs.length;i++) {
			regex += "(" + "(" +  disallowedVerbs[i] + ")" + ".*)";
			
			if (i != disallowedVerbs.length-1) {
				regex += "|";
			}
		}
		regex += "|";
		for (int i=0;i<disallowedSpells.length;i++) {
			regex += "(" + "\\w+\\s" + "(" + disallowedSpells[i] + ")" + ".*)";
			
			if (i != disallowedSpells.length-1)
				regex += "|";
		}
		regex += "|";
		for (int i=0;i<disallowedBuffs.length;i++) {
			regex += "(" + "((gains)|(loses))\\s" + "(" + disallowedBuffs[i] + ")(\\s.+)?)";
			if (i != disallowedBuffs.length-1) 
				regex += "|";
		}
		
		regex += "))|(" + regexStart + "\\w+\\s(hits)\\s[^\\s]+\\s(for)\\s\\d+.*)" // Remove lines that don't start with a number
				+ "|([^0-9]+.*)" // Remove lines with "hits" in it
				+ "|(\\r)"
				+ "|(^$)"; //empty string 
		
		regex3Lines = regexStart + "(";
		for (int i=0;i<disallowed3LineSpells.length;i++) {
			regex3Lines += "(" + "(performs)\\s" + "(" + disallowed3LineSpells[i] + ")" + ".*)";
			
			if (i != disallowed3LineSpells.length-1) 
				regex3Lines += "|";
		}
		regex3Lines += ")";
	}
	
	
	/**
	 * Returns how many lines should be ignored: 
	 * 1 means ignore this line, 
	 * 2 means ignore this and next
	 * @param line
	 * @return
	 */
	public int lineMatches(String line) {
		if (line.matches(regex))
			return 1;
		if (line.matches(regex3Lines)) 
			return 3;
		
		return 0;
	}
	
	public static void main(String[] args) {
		
	}	
	
}
