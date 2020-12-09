/*
*AND-Tree Search applying Branch-and-Bound 
*Names:
*Mitchel Belanger, UCID: 30075310
*Sean Park, UCID: 30061734
*Tadic Adrian, UCID 30077647
*
*/


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
		LinkedList<Leaf> leaves = new LinkedList<Leaf>();
		leaves.add(root);
		State tree = new State(leaves, ReaderThing.getDebug());
		root.setConstr(c);
		root.setEval(c);
		
		System.out.println("Starting search.");
		
		while(!tree.goal(0)){
			if (ReaderThing.getDebug())
				System.out.println("AA");
			tree.erw(c);
		}
		
		if (tree.leaves.size() !=0){
			// System.out.println("Solution:");
			Prob solution = tree.bestLeaf.pr;
			// System.out.println("Eval-value: " + solution.eval);
			
			HashMap<ClassLab, Slot> scheduleByClasses = new HashMap<ClassLab, Slot>();
			
			for (Slot sl : solution.getUnmodifiableSlots()){
				// String output = solution.courseSlots.get(i).toString();
				// System.out.println(output);
				
				for (ClassLab cl : sl.getUnmodifiableClasses()) {
					scheduleByClasses.put(cl, sl);
				}
			}
			
			List<ClassLab> classes = ReaderThing.getCourses().stream()
					.sorted((cl1, cl2) -> cl1.toString().compareTo(cl2.toString()))
					.collect(Collectors.toList());
			
			try {
				File out = new File("outputs/out_" + args[0]);
				BufferedWriter writer = new BufferedWriter(new FileWriter(out));
				
				writer.write("Eval-value: " + solution.eval + "\n");
				for (ClassLab cl : classes) {
					writer.write(String.format("%1$-28s:%2$s\n", cl.toString(), scheduleByClasses.get(cl).toMinimalString()));
				}
				
				writer.flush();
				writer.close();
				
				System.out.println("Wrote solution to " + out.getName());
				
			} catch (Exception e) {
				System.out.println(e);
			}
			
		}
		else{
			System.out.println("The problem was unsolvable.");
		}
		
		// testing
		/*
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
		*/
		
		System.out.println("Done.");
	}

	
	
}