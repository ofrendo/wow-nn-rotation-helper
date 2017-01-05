package de.nnrh.preparation;

public enum LineType {
	
	SPELL			("\\d+\\.\\d{3}\\s\\w+\\s(performs)\\s.*"),
	ITEM			("\\d+\\.\\d{3}\\s\\w+\\s(schedules)\\s(execute)\\s(for)\\s(use_item_\\w+)"),
	// debuffs need to be first because it is a greedy approach and they are more specific
	DEBUFF_GAIN		("\\d+\\.\\d{3}\\s(Fluffy_Pillow)\\s(gains)\\s.*"),
	DEBUFF_LOSS		("\\d+\\.\\d{3}\\s(Fluffy_Pillow)\\s(loses)\\s.*"),
	BUFF_GAIN		("\\d+\\.\\d{3}\\s\\w+\\s(gains)\\s.*"),
	BUFF_LOSS		("\\d+\\.\\d{3}\\s\\w+\\s(loses)\\s.*");
	
	public String regex;
	
	LineType(String regex) {
		this.regex = regex;
	}
	
}
