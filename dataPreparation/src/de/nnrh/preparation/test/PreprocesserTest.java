package de.nnrh.preparation.test;

import org.junit.Assert;
import org.junit.Test;

import de.nnrh.preparation.Preprocesser;

public class PreprocesserTest {
	
	Preprocesser p = new Preprocesser();
	
	public void testLines(String lines, int expectedLines) {
		String newContent = p.checkActivePassiveSpells(lines);
		if (newContent.split("\r\n").length != expectedLines) {
			//String out = "Expected " + expected + ", was " + result + " (" + line + ")";
			//System.out.println(out);
			System.out.println(lines);
			System.out.println("Expected " + expectedLines + " lines");
			System.out.println(newContent);
			Assert.fail(lines);
		}
	}

	@Test
	public void test() {
		
		testLines("1.297 Zhadokmage schedules execute for meteor\r\n" + 
				  "1.297 Zhadokmage performs meteor (1148901)",
				  1);	
		
		
	}

}
