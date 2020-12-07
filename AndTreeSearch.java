/*
*AND-Tree Search applying Branch-and-Bound 
*Names:
*Mitchel Belanger, UCID: 30075310
*
*/

/*import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException; 
import java.io.File;
import java.util.ArrayList;*/


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
		
		System.out.println("done for now");
	}

	
	
}