import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

public class ReaderThing {
	private static String faculty; //CPSC, SENG, etc.
    private static String courseNumber;   //the 433 in CPSC 433
    private static int courseSection;  //section 1, 2, etc., set to 0 if its a lab for every lecture section
    private static boolean isLab;  //true if this is a lab
	private static int labSection; //if its a lab, set to the correct lab section, set to 0 if this is a lecture
	private static ArrayList<DayOfWeek> days;
    private static LocalTime startTime;
    private static LocalTime endTime;
    private static int courseMax;  //coursmax is also labmax for the slot if this is a lab
	private static int courseMin;  //see above
	private static String[] words;
	private static UniClass classLabToAdd;
	private static Slot courseSlotToAdd;
	private static ArrayList<UniClass> stuffToBePlaced = new ArrayList<UniClass>();
	private static ArrayList<Slot> courseSlots = new ArrayList<Slot>();

	
	
	private static UniClass constructClass(String identifier) {
		
		
		return null;
	}
	
	private static UniClass searchForUniClass(String identifier, ArrayList<UniClass> uniClasses) {
		
		
		return null;
	}

	public static void main(String args[]){
		ArrayList<String> input = collectInput(args); 
		int i = 0;
		String workingOn;
		/*while((!input.isEmpty())&&(i < input.size())){
			System.out.println(input.get(i));
			i++;
		}*/
		i=0;
		//this is just file name
		while (input.get(i).length()>2){   //(!input.isEmpty())&&(input.get(i).length()>2)
			workingOn = (input.get(i));
			System.out.println(workingOn);
			i++;
		}
		i++;


		//this is course slots
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Course"))){
				//idea: delete the commas, split by space, each index is now a part of the original string, so i can access Mo separately from 8:00 etc.
				workingOn = workingOn.replaceAll(",","");
				words = workingOn.split(" ");

				
				//if the time doesnt start with 0 or 1 or 2, then add a leading zero, so 8:00 becomes 08:00, do this because LocalTime.parse requires hours be two digits.
				if (!((words[1].charAt(0) == '0')||(words[1].charAt(0) == '1')||(words[1].charAt(0) == '2'))){
					startTime = LocalTime.parse("0"+words[1]);
				}
				else{
					startTime = LocalTime.parse(words[1]);
				}
				
				if (words[0] == "MO") {
					days = new ArrayList<DayOfWeek>(Arrays.asList(
							new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY}));
					
					endTime = startTime.plusHours(1);
					
				} else if (words[0] == "TU") {
					days = new ArrayList<DayOfWeek>(Arrays.asList(
							new DayOfWeek[] {DayOfWeek.TUESDAY, DayOfWeek.THURSDAY}));

					endTime = startTime.plusMinutes(90);
					
				}
				courseMax = Integer.parseInt(words[2]);
				courseMin = Integer.parseInt(words[3]);
				
				courseSlotToAdd = new Slot(days,startTime, endTime,courseMax,courseMin, false);
				courseSlots.add(courseSlotToAdd);
			}
			i++;
		}


		i++;
		//this is lab slots
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Lab"))){
				workingOn = workingOn.replaceAll(",","");
				words = workingOn.split(" ");
				
				if (!((words[1].charAt(0) == '0')||(words[1].charAt(0) == '1')||(words[1].charAt(0) == '2'))){
					startTime = LocalTime.parse("0"+words[1]);
				}
				else{
					startTime = LocalTime.parse(words[1]);
				}
				
				if (words[0] == "MO") {
					days = new ArrayList<DayOfWeek>(Arrays.asList(
							new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY}));
					
					endTime = startTime.plusHours(1);
					
				} else if (words[0] == "FR") {
					days = new ArrayList<DayOfWeek>(Arrays.asList(
							new DayOfWeek[] {DayOfWeek.FRIDAY}));
					
					endTime = startTime.plusHours(2);
					
				} else if (words[0] == "TU") {
					days = new ArrayList<DayOfWeek>(Arrays.asList(
							new DayOfWeek[] {DayOfWeek.TUESDAY, DayOfWeek.THURSDAY}));

					endTime = startTime.plusHours(1);
					
				}
				
				courseMax = Integer.parseInt(words[2]);
				courseMin = Integer.parseInt(words[3]);
				
				courseSlotToAdd = new Slot(days,startTime, endTime,courseMax,courseMin, true);
				courseSlots.add(courseSlotToAdd);
			}
			i++;
		}


		i++;
		//this is courses
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			
			if (!(workingOn.contains("Course"))){
				words = workingOn.split(" ");
				faculty = words[0];
				courseNumber = words[1];
				courseSection = Integer.parseInt(words[3]);
				classLabToAdd = new UniClass(faculty, courseNumber, courseSection, false, 0);
				stuffToBePlaced.add(classLabToAdd);
			}
			i++;
		}


		i++;
		//this is labs
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			
			if (!(workingOn.contains("Lab"))){
				words = workingOn.split(" ");
				faculty = words[0];
				courseNumber = words[1];
				
				if (words[2].equals("LEC")){
					courseSection = Integer.parseInt(words[3]);
					labSection = Integer.parseInt(words[5]);
				}
				else {
					courseSection = 0;
					labSection = Integer.parseInt(words[3]);
				}
				
				classLabToAdd = new UniClass(faculty, courseNumber, courseSection, true, labSection);
				stuffToBePlaced.add(classLabToAdd);
			}
			i++;
		}


		i++;
		//this is not compatible
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Not"))){
				//at this point workonOn is a string similar to: CPSC 567 LEC 01, CPSC 433 LEC 01
				String[] cls = workingOn.split(" ");
				
				
				
			}
			i++;
		}
		i++;
		//this is unwanted
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Unw"))){
				//at this point workingOn is a string similar to: CPSC 433 LEC 01, MO, 8:00
			}
			i++;
		}


		i++;
		//this is preferences
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Pre"))){

			}
			i++;
		}


		i++;
		//this is pair
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Pai"))){
				//at this point workingOn is a string similar to: SENG 311 LEC 01, CPSC 567 LEC 01
			}
			i++;
		}


		i++;
		//this is partial assignments
		while (i < input.size()){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Part"))){
				//at this point workingOn is a string similar to: SENG 311 LEC 01, MO, 8:00
			}
			i++;
		}
	}

	public static ArrayList<String> collectInput(String[] args){
		ArrayList<String> totalInput = new ArrayList<String>();		
		//File inputFile = new File(args[0]);//args[0] in brackets
		try{ 
		BufferedReader input = new BufferedReader(new FileReader("inputs/gehtnicht5.txt")); //should look something like "inputs/" + inputFile
		String line = ""; 
		while((line = input.readLine()) != null){
			totalInput.add(line);
		}
		input.close();
		} catch(FileNotFoundException er){
			System.err.println("File not found.");
		} catch(IOException er){
			System.err.println("Unable to read file.");
		}
		return totalInput;
	}

	public static ArrayList<UniClass> getCourses() {
		return stuffToBePlaced;
	}

	public static ArrayList<Slot> getSlots() {
		return courseSlots;
	}
}