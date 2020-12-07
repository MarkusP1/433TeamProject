import java.util.ArrayList;

/*
*AND-Tree Search applying Branch-and-Bound 
*Names:
*Mitchel Belanger, UCID: 30075310
*Sean Park, UCID: 30061734
*
*/



public class AndTreeSearch {
	 State startState;

	/**
	 * 
	 * @param args
	 * takes in 10 inputs:
	 * 	1: input file
	 * 	2 - 5: pen_coursemin, pen_labsmin, pen_notpaired, pen_section
	 * 	6 - 9: w_minfilled, w_pref, w_pair, w_secdiff
	 * 	10: true/false debug mode (to be implemented for printing everything and waiting for input to continue)
	 */
	public static void main(String[] args) {
		ReaderThing.read(args);
		
		
		Leaf root = new Leaf(new Prob(ReaderThing.getCourses(), ReaderThing.getSlots()), 0);
		ConstraintChecker c = ReaderThing.getConstraintChecker();
		
		// testing
		ArrayList<ClassLab> testClasses = new ArrayList<ClassLab>(ReaderThing.getCourses());
		ArrayList<Slot> testSlots = new ArrayList<Slot>(ReaderThing.getSlots());
		
		ClassLab lastcl = testClasses.get(testClasses.size() - 1);
		testClasses.remove(lastcl);
		testSlots.get(3).addClassLab(lastcl);
		lastcl = testClasses.get(testClasses.size() - 1);
		testClasses.remove(lastcl);
		testSlots.get(3).addClassLab(lastcl);
		
		Prob testProb = new Prob(testClasses, testSlots);
		
		c.constrStar(testProb);
		System.out.println(testProb.getConstr());
		if (testProb.getConstr()) {
			c.evalStar(testProb);
			System.out.println(testProb.getEval());
		}
			
		
		System.out.println("done for now");
	}

	
	
}