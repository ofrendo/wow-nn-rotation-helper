package de.nnrh.preparation.test;

import org.junit.Assert;
import org.junit.Test;

import de.nnrh.preparation.LineMatcher;

public class LineMatcherTest {

	LineMatcher lm = new LineMatcher();
	
	public void testLine(String line, int expected) {
		int result = lm.lineMatches(line);
		if (result != expected) {
			String out = "Expected " + expected + ", was " + result + " (" + line + ")";
			System.out.println(lm.regex);
			System.out.println(out);
			Assert.fail(out);
		}
	}
	
	@Test
	public void testLineMatcher() {
		
		// lines at the start
		testLine("0.000 Zhadokmage performs flask (1155000)", 3);
		testLine("0.000 Zhadokmage performs food (1155000)", 3);
		testLine("0.000 Zhadokmage performs augmentation (1155000)", 3);
		
		// Invalid verbs
		testLine("0.000 Fluffy_Pillow tries to arise.", 1);
		testLine("249.924 Zhadokmage schedules execute for pyroblast", 0);
		testLine("0.000 Zhadokmage schedules travel (1.200) for deadly_grace", 1);
		
		// Invalid buffs
		testLine("300.000 Zhadokmage loses 1300.00 intellect (temporary)", 1);
		testLine("300.000 Zhadokmage gains 1300.00 intellect (temporary)", 1);
		testLine("300.000 Zhadokmage gains casting (temporary)", 1);
		testLine("0.000 Zhadokmage gains casting_1 ( value=-0.00 )", 1);
		testLine("1.294 Zhadokmage loses casting", 1);
		testLine("0.000 Fluffy_Pillow gains Health Decade (90 - 100)_1 ( value=-0.00 )", 1);
		testLine("0.000 Fluffy_Pillow gains mortal_wounds_1 ( value=0.25 )", 1);
		testLine("0.000 Fluffy_Pillow gains bleeding_1 ( value=1.00 )", 1);	
		
		testLine("0.000 Fluffy_Pillow gains bleeding_12 ( value=1.00 )", 0); // other bleeding debuffs should be allowed		
		testLine("0.000 Zhadokmage gains potion_of_deadly_grace_1 ( value=-0.00 )", 0);
		testLine("300.000 Zhadokmage loses flask_of_the_whispered_pact", 1);
		
		// Remove "hit" messages
		testLine("0.750 Zhadokmage pyroblast hits Fluffy_Pillow for 431028 fire damage (crit)", 1);
		testLine("1.420 Zhadokmage ignite spread event occurs", 1);
		testLine("1.783 Zhadokmage ignite ticks (1 of 9) Fluffy_Pillow for 8279 fire damage (hit)", 1);


		// Lines at the end
		testLine(" 3 RNG Engine    = sse2-sfmt", 1);
		testLine("\r", 1);
		testLine("\n", 1);
		testLine("", 1);
		
		
		// Allowed lines
		testLine("0.000 Zhadokmage performs pyroblast (1155000)", 0);
		testLine("0.000 Zhadokmage performs potion (1155000)", 0);
	}

}
