import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ReaderThing {
	private static ArrayList<ClassLab> stuffToBePlaced = new ArrayList<ClassLab>();
	private static ArrayList<Slot> slots = new ArrayList<Slot>();
	private static HashMap<ClassLab, ClassLabConstraints> constraintsMap;
	private static ConstraintChecker c;
	
	private static ArrayList<String> input; 

	private static int pen_coursemin;
	private static int pen_labsmin;
	private static int pen_notpaired;
	private static int pen_section;
	
	private static float w_minfilled;
	private static float w_pref;
	private static float w_pair;
	private static float w_secdiff;
	
	private static boolean debug;

	private static Slot constructSlot(String[] identifier, boolean isLab) {
		ArrayList<DayOfWeek> days;
	    LocalTime startTime;
	    LocalTime endTime;
	    int courseMax;  //coursmax is also labmax for the slot if this is a lab
		int courseMin;  //see above
		
		//if the time doesnt start with 0 or 1 or 2, then add a leading zero, so 8:00 becomes 08:00, do this because LocalTime.parse requires hours be two digits.
		if (!((identifier[1].charAt(0) == '0')||(identifier[1].charAt(0) == '1')||(identifier[1].charAt(0) == '2'))) {
			startTime = LocalTime.parse("0"+identifier[1]);
		}
		else{
			startTime = LocalTime.parse(identifier[1]);
		}
		
		if (identifier[0].equals("MO")) {
			if (isLab)
				days = new ArrayList<DayOfWeek>(Arrays.asList(
						new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY}));
			else
				days = new ArrayList<DayOfWeek>(Arrays.asList(
						new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, 
								DayOfWeek.FRIDAY}));
			
			endTime = startTime.plusHours(1);
			
		} else if (identifier[0].equals("FR")) {
			days = new ArrayList<DayOfWeek>(Arrays.asList(
					new DayOfWeek[] {DayOfWeek.FRIDAY}));
			
			endTime = startTime.plusHours(2);
			
		} else if (identifier[0].equals("TU")) {
			days = new ArrayList<DayOfWeek>(Arrays.asList(
					new DayOfWeek[] {DayOfWeek.TUESDAY, DayOfWeek.THURSDAY}));

			if (isLab)
				endTime = startTime.plusHours(1);
			else
				endTime = startTime.plusMinutes(90);
			
		} else {
			// should not be reached
			return null;
		}
		
		if (identifier.length == 4) {
			courseMax = Integer.parseInt(identifier[2]);
			courseMin = Integer.parseInt(identifier[3]);
		} else {
			courseMax = 0;
			courseMin = 0;
		}
		
		return new Slot(days, startTime, endTime, courseMax, courseMin, isLab);
	}
	
	private static ClassLab constructUniClass(String identifier) {
		String faculty; //CPSC, SENG, etc.
	    String courseNumber;   //the 433 in CPSC 433
	    int courseSection;  //section 1, 2, etc., set to 0 if its a lab for every lecture section
	    boolean isLab;  //true if this is a lab
		int labSection; //if its a lab, set to the correct lab section, set to 0 if this is a lecture
		
		faculty = identifier.substring(0, 4);
		courseNumber = identifier.substring(4, 7);
		
		if (identifier.substring(7, 10).equals("LEC")){
			courseSection = Integer.parseInt(identifier.substring(10, 12));
			
			if (identifier.length() == 12) {
				// is a lecture
				
				isLab = false;
				labSection = 0;
			} else {
				// is a lab specific to a certain course section
				
				isLab = true;
				labSection = Integer.parseInt(identifier.substring(15, 17));
			}
		} else {
			// is a lab open to all course sections

			isLab = true;
			courseSection = 0;
			labSection = Integer.parseInt(identifier.substring(10, 12));
		}
		
		return new ClassLab(faculty, courseNumber, courseSection, isLab, labSection);
	}

	private static void trickyConstraint(String constrfaculty, String consrtcourseNumber,
			String trickfaculty, String trickycourseNumber, Slot trickyComparatorSlot) {
		List<ClassLab> constrcls = stuffToBePlaced.stream()
				.filter(cl -> cl.getFaculty().equals(constrfaculty) 
						&& cl.getCourseNumber().equals(consrtcourseNumber))
				.collect(Collectors.toList());
		List<ClassLab> trickycls = stuffToBePlaced.stream()
				.filter(cl -> cl.getFaculty().equals(trickfaculty) 
						&& cl.getCourseNumber().equals(trickycourseNumber))
				.collect(Collectors.toList());
		
		if (constrcls.size() != 0) {
			// CPSC 413 is included
			if (trickycls.size() == 0) {
				ClassLab trickycl = new ClassLab(trickfaculty, trickycourseNumber, 1, false, 0);
				trickycls.add(trickycl);
				constraintsMap.put(trickycl, new ClassLabConstraints());
			}
			
			Slot trickysl = slots.stream().filter(sl -> sl.weakEquals(trickyComparatorSlot))
					.findAny().orElse(null);
			
			if (trickysl == null) {
				trickysl = trickyComparatorSlot;
				slots.add(trickysl);
				
			}
			
			for (ClassLab tricky : trickycls) {
				stuffToBePlaced.remove(tricky);
				trickysl.addClassLab(tricky);
			
				// fill not compatible contraint
				for (ClassLab constrcl : constrcls) {
					ClassLabConstraints toCopy = constraintsMap.get(constrcl);
					ClassLabConstraints toFill = constraintsMap.get(tricky);
					
					for (ClassLab cl : toCopy.getUnmodifiableNotCompatible()) {
						if (!toFill.notCompatibleContains(cl)) {
							toFill.addNotCompatible(cl);
							constraintsMap.get(cl).addNotCompatible(tricky);
						}
					}
					if (!toFill.notCompatibleContains(constrcl)) {
						toFill.addNotCompatible(constrcl);
						constraintsMap.get(constrcl).addNotCompatible(tricky);
					}
				}
			}
		}
	}
	
	// will fill stuffToBePlaced and slots, and will initialize constraintsMap
	public static void read(String args[]){
		/*String faculty; //CPSC, SENG, etc.
	    String courseNumber;   //the 433 in CPSC 433
	    int courseSection;  //section 1, 2, etc., set to 0 if its a lab for every lecture section
	    boolean isLab;  //true if this is a lab
		int labSection; //if its a lab, set to the correct lab section, set to 0 if this is a lecture
		ArrayList<DayOfWeek> days;
	    LocalTime startTime;
	    LocalTime endTime;
	    int courseMax;  //coursmax is also labmax for the slot if this is a lab
		int courseMin;*/ //see above
		String identifier;
		/*UniClass classLabToAdd;
		Slot courseSlotToAdd;*/
		
		collectInput(args);
		
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

				workingOn = workingOn.replaceAll(" ","");

				/*
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
				courseMin = Integer.parseInt(words[3]);*/
				
				slots.add(constructSlot(workingOn.split(","), false));
			}
			i++;
		}


		i++;
		//this is lab slots
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Lab"))){
				workingOn = workingOn.replaceAll(" ","");
				
				/*if (!((words[1].charAt(0) == '0')||(words[1].charAt(0) == '1')||(words[1].charAt(0) == '2'))){
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
				courseMin = Integer.parseInt(words[3]);*/
				
				slots.add(constructSlot(workingOn.split(","), true));
			}
			i++;
		}


		i++;
		//this is courses
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			
			if (!(workingOn.contains("Course"))){
				identifier = workingOn.replaceAll(" ", "");
				/*faculty = words[0];
				courseNumber = words[1];
				courseSection = Integer.parseInt(words[3]);
				classLabToAdd = new UniClass(faculty, courseNumber, courseSection, false, 0);*/
				stuffToBePlaced.add(constructUniClass(identifier));
			}
			i++;
		}


		i++;
		//this is labs
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			
			if (!(workingOn.contains("Lab"))){
				identifier = workingOn.replaceAll(" ", "");
				/*faculty = words[0];
				courseNumber = words[1];
				
				if (words[2].equals("LEC")){
					courseSection = Integer.parseInt(words[3]);
					labSection = Integer.parseInt(words[5]);
				}
				else {
					courseSection = 0;
					labSection = Integer.parseInt(words[3]);
				}
				classLabToAdd = new UniClass(faculty, courseNumber, courseSection, true, labSection);*/
				stuffToBePlaced.add(constructUniClass(identifier));
			}
			i++;
		}

		constraintsMap = (HashMap<ClassLab, ClassLabConstraints>) stuffToBePlaced.stream().collect(Collectors.toMap(
				cl -> cl, cl -> new ClassLabConstraints()));

		i++;
		//this is not compatible
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Not"))){
				//at this point workonOn is a string similar to: CPSC 567 LEC 01, CPSC 433 LEC 01
				String[] cls = workingOn.split(",");
				ClassLab parsedcl1 = constructUniClass(cls[0].replaceAll(" ", ""));
				ClassLab parsedcl2 = constructUniClass(cls[1].replaceAll(" ", ""));
				
				ClassLab storedcl1 = stuffToBePlaced.stream().filter(cl -> cl.equals(parsedcl1))
						.findAny().orElse(null);
				ClassLab storedcl2 = stuffToBePlaced.stream().filter(cl -> cl.equals(parsedcl2))
						.findAny().orElse(null);
				
				constraintsMap.get(storedcl1).addNotCompatible(storedcl2);
				constraintsMap.get(storedcl2).addNotCompatible(storedcl1);
				
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
				workingOn = workingOn.replaceAll(" ", "");
				String[] words = workingOn.split(",");
				ClassLab parsedcl = constructUniClass(words[0]);
				Slot parsedsl = constructSlot(Arrays.copyOfRange(words, 1, 3), parsedcl.isLab());
				
				ClassLab storedcl = stuffToBePlaced.stream().filter(cl -> cl.equals(parsedcl))
						.findAny().orElse(null);
				Slot storedsl = slots.stream().filter(sl -> sl.weakEquals(parsedsl))
						.findAny().orElse(null);
				
				constraintsMap.get(storedcl).addUnwanted(storedsl);
			}
			i++;
		}


		i++;
		//this is preferences
		while (input.get(i).length()>2){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Pre"))){
				workingOn = workingOn.replaceAll(" ", "");
				String[] words = workingOn.split(",");
				ClassLab parsedcl = constructUniClass(words[2]);
				Slot parsedsl = constructSlot(Arrays.copyOfRange(words, 0, 2), parsedcl.isLab());
				int prefRating = Integer.parseInt(words[3]);
				
				ClassLab storedcl = stuffToBePlaced.stream().filter(cl -> cl.equals(parsedcl))
						.findAny().orElse(null);
				Slot storedsl = slots.stream().filter(sl -> sl.weakEquals(parsedsl))
						.findAny().orElse(null);
				
				ClassLabConstraints constraints = constraintsMap.get(storedcl);
				constraints.setPreference(storedsl);
				constraints.setPen_notInPreference(prefRating);
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
				workingOn = workingOn.replaceAll(" ", "");
				String[] cls = workingOn.split(",");
				ClassLab parsedcl1 = constructUniClass(cls[0]);
				ClassLab parsedcl2 = constructUniClass(cls[1]);
				
				ClassLab storedcl1 = stuffToBePlaced.stream().filter(cl -> cl.equals(parsedcl1))
						.findAny().orElse(null);
				ClassLab storedcl2 = stuffToBePlaced.stream().filter(cl -> cl.equals(parsedcl2))
						.findAny().orElse(null);
				
				constraintsMap.get(storedcl1).addPair(storedcl2);
				constraintsMap.get(storedcl2).addPair(storedcl1);
			}
			i++;
		}


		i++;
		//this is partial assignments
		while (i < input.size() && !input.get(i).equals("")){
			workingOn = (input.get(i));
			System.out.println(workingOn);
			if (!(workingOn.contains("Part"))){
				//at this point workingOn is a string similar to: SENG 311 LEC 01, MO, 8:00
				workingOn = workingOn.replaceAll(" ", "");
				String[] words = workingOn.split(",");
				ClassLab parsedcl = constructUniClass(words[0]);
				Slot parsedsl = constructSlot(Arrays.copyOfRange(words, 1, 3), parsedcl.isLab());
				
				ClassLab storedcl = stuffToBePlaced.stream().filter(cl -> cl.equals(parsedcl))
						.findAny().orElse(null);
				Slot storedsl = slots.stream().filter(sl -> sl.weakEquals(parsedsl))
						.findAny().orElse(null);
				
				constraintsMap.get(storedcl).setPartassign(storedsl);
			}
			i++;
		}
		
		
		// deal with CPSC 313/413 and CPSC 813/913

		trickyConstraint("CPSC", "313", "CPSC", "813", constructSlot(new String[] {"TU", "18:00", "1", "0"}, true));
		trickyConstraint("CPSC", "413", "CPSC", "913", constructSlot(new String[] {"TU", "18:00", "1", "0"}, true));
		
		c = new ConstraintChecker(constraintsMap, pen_coursemin, pen_labsmin, pen_notpaired, 
				pen_section, w_minfilled, w_pref, w_pair, w_secdiff, debug);
		

		System.out.println("Done parsing file");
		if (debug) {
			System.out.println(stuffToBePlaced);
			System.out.println(slots);
			System.out.println(constraintsMap);
			
		}
	}

	public static void collectInput(String[] args){
		ArrayList<String> totalInput = new ArrayList<String>();		
		File inputFile = new File("inputs/" + args[0]);//args[0] in brackets
		try{ 
		BufferedReader reader = new BufferedReader(new FileReader(inputFile/*"inputs/gehtnicht5.txt"*/)); //should look something like "inputs/" + inputFile
		String line = ""; 
		while((line = reader.readLine()) != null){
			totalInput.add(line);
		}
		reader.close();
		} catch(FileNotFoundException er){
			System.err.println("File not found.");
		} catch(IOException er){
			System.err.println("Unable to read file.");
		}
		
		input = totalInput;

		pen_coursemin = Integer.parseInt(args[1]);
		pen_labsmin = Integer.parseInt(args[2]);
		pen_notpaired = Integer.parseInt(args[3]);
		pen_section = Integer.parseInt(args[4]);
		
		w_minfilled = Float.parseFloat(args[5]);
		w_pref = Float.parseFloat(args[6]);
		w_pair = Float.parseFloat(args[7]);
		w_secdiff = Float.parseFloat(args[8]);
		
		debug = Boolean.parseBoolean(args[9]);
	}

	public static ArrayList<ClassLab> getCourses() {
		return stuffToBePlaced;
	}

	public static ArrayList<Slot> getSlots() {
		return slots;
	}

	public static ConstraintChecker getConstraintChecker() {
		return c;
	}
	
	public static boolean getDebug() {
		return debug;
	}
}