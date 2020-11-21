/*
*AND-Tree Search applying Branch-and-Bound 
*Names:
*Mitchel Belanger, UCID: 30075310
*
*/

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException; 
import java.io.File;
import java.util.ArrayList;


public class And-Tree-Search {
	
/*
* 
*/
	public class SlotObj(String day, String time, int course/labmax, int course/labmin, String ClassOrLab){
		String slotDay = day;
		String slotTime = time;
		int course/Labmax = course/labmax;
		int course/Labmin = course/labmin;
		String slotType = ClassOrLab;
		Arraylist<CoursesULabsObj> setOfContainedClasses = new Arraylist<CoursesULabsObj>;
	}
	
/*
*@param Identifier is the total name of a class / lab i.e.:  CPSC 433 LEC 01 is split into CPSC, 433, Lec, 1 
*Need to specify the case for tutorials:  
*/
	public class CoursesULabsObj((String[] identifierArray = String identifier.split(" "))){
		String faculty = identifierArray[0];
		int courseNumber = int identifierArray[1];
		int lecSection = int identifierArray[2]; 
		int tutorial_or_lab = int identifierArray[3]; 
		int labSection = int identifierArray[4]; 
	}

/*
*
*
*/
	public class State{
		//(Pr, sol)
		//Can have an empty list of children, or a populated list of children. 
	}
	
	public class Pr(){
		Arraylist<SlotObj> ListSlots = //Method that yields ListSlots from "TotalInputList"
		ArrayList<CoursesULabsObj> ListCoursesorLabs = //Method that yields ListcourseorLabs from "TotalInputList"
		
		
	}
	
	public ArrayList<SlotObj>
	
	public static State StartState = new State();
	




}