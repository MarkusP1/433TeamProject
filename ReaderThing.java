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

	
	/**
	 * Constructs a new Slot object from the given identifying string and whether it's a lab or not.
	 * @param identifier
	 * 	A string array with meaningful indices:
	 * 		0 : first of the days
	 * 		1 : start time
	 * 		2 : coursemax/labmax
	 * 		3 : coursemin/labmin
	 * @param isLab
	 * 	Indicates whether the slot is specific to labs or not.
	 * @return Constructed Slot object.
	 */
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
	
	
	/**
	 * Constructs a new ClassLab object from the given identifier string.
	 * @param identifier
	 * 	A string in the format of:
	 * 		faculty + courseNumber + (LEC + courseSection) + (LAB / TUT + labSection)
	 * @return Constructed ClassLab object.
	 */
	private static ClassLab constructClassLab(String identifier) {
		String faculty; //CPSC, SENG, etc.
	    String courseNumber;   //the 433 in CPSC 433
	    int courseSection;  //section 1, 2, etc., set to 0 if its a lab for every lecture section
	    boolean isLabOrTut;  //true if this is a lab or tut
	    boolean isTut; // true if this is a tut
		int labSection; //if its a lab, set to the correct lab section, set to 0 if this is a lecture
		
		faculty = identifier.substring(0, 4);
		courseNumber = identifier.substring(4, 7);
		
		if (identifier.substring(7, 10).equals("LEC")){
			courseSection = Integer.parseInt(identifier.substring(10, 12));
			
			if (identifier.length() == 12) {
				// is a lecture
				
				isLabOrTut = false;
				isTut = false;
				labSection = 0;
			} else {
				// is a lab or tut specific to a certain course section
				
				isLabOrTut = true;
				isTut = identifier.substring(12, 15).equals("TUT");
				labSection = Integer.parseInt(identifier.substring(15, 17));
			}
		} else {
			// is a lab or tut open to all course sections
			
			isLabOrTut = true;
			isTut = identifier.substring(7, 10).equals("TUT");
			courseSection = 0;
			labSection = Integer.parseInt(identifier.substring(10, 12));
		}
		
		return new ClassLab(faculty, courseNumber, courseSection, isLabOrTut, isTut, labSection);
	}

	
	/**
	 * Parses command line arguments, reading lines of the input file and initializing
	 * penalty and weight values.
	 * 
	 * @param args
	 * 	Command line arguments to parse.
	 */
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

	
	/**
	 * Modifies the the list of classes and the constraints map to implement the tricky
	 * constraints involving 313/413 and 813/913. Generalized to any constraining class
	 * and tricky class which is placed in a tricky slot.
	 * 
	 * When a constraining class is present, if a tricky class does not exist then it is
	 * created. Then if a tricky slot does not exist then it is created with max of 1.
	 * Then the tricky class is assigned to the tricky slot, and constraints are added to
	 * the tricky class based on the constraining class.
	 * 
	 * @param constrfaculty Constraining class faculty.
	 * @param constrcourseNumber Constraining class course number.
	 * @param trickyfaculty Tricky class faculty.
	 * @param trickycourseNumber Tricky class course number.
	 * @param trickyComparatorSlot Tricky slot in which the tricky class will be assigned.
	 */
	private static void trickyConstraint(String constrfaculty, String constrcourseNumber,
			String trickyfaculty, String trickycourseNumber, Slot trickyComparatorSlot) {
		List<ClassLab> constrcls = stuffToBePlaced.stream()
				.filter(cl -> cl.getFaculty().equals(constrfaculty) 
						&& cl.getCourseNumber().equals(constrcourseNumber))
				.collect(Collectors.toList());
		List<ClassLab> trickycls = stuffToBePlaced.stream()
				.filter(cl -> cl.getFaculty().equals(trickyfaculty) 
						&& cl.getCourseNumber().equals(trickycourseNumber))
				.collect(Collectors.toList());
		
		if (constrcls.size() != 0) {
			// CPSC 313/413 is included
			if (trickycls.size() == 0) {
				// CPSC 813/913 is not included
				ClassLab trickycl = new ClassLab(trickyfaculty, trickycourseNumber, 1, false, false, 0);
				trickycls.add(trickycl);
				constraintsMap.put(trickycl, new ClassLabConstraints());
			}
			
			Slot trickysl = slots.stream().filter(sl -> sl.weakEquals(trickyComparatorSlot))
					.findAny().orElse(null);
			
			if (trickysl == null) {
				// slot for CPSC 813/913 doesn't exist
				trickysl = trickyComparatorSlot;
				slots.add(trickysl);
				
			}
			
			for (ClassLab tricky : trickycls) {
				stuffToBePlaced.remove(tricky);
				trickysl.addClassLab(tricky);
			
				// fill not compatible contraints
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
		String identifier;

		collectInput(args);
		
		int i = 0;
		String workingOn;
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

				stuffToBePlaced.add(constructClassLab(identifier));
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

				stuffToBePlaced.add(constructClassLab(identifier));
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
				ClassLab parsedcl1 = constructClassLab(cls[0].replaceAll(" ", ""));
				ClassLab parsedcl2 = constructClassLab(cls[1].replaceAll(" ", ""));
				
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
				ClassLab parsedcl = constructClassLab(words[0]);
				Slot parsedsl = constructSlot(Arrays.copyOfRange(words, 1, 3), parsedcl.isLabOrTut());
				
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
				ClassLab parsedcl = constructClassLab(words[2]);
				Slot parsedsl = constructSlot(Arrays.copyOfRange(words, 0, 2), parsedcl.isLabOrTut());
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
				ClassLab parsedcl1 = constructClassLab(cls[0]);
				ClassLab parsedcl2 = constructClassLab(cls[1]);
				
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
				ClassLab parsedcl = constructClassLab(words[0]);
				Slot parsedsl = constructSlot(Arrays.copyOfRange(words, 1, 3), parsedcl.isLabOrTut());
				
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
		
		
		// rank the classes by 'how constrained' they are; measurement is a rudimentary method
		stuffToBePlaced.sort((cl1, cl2) -> {
					ClassLabConstraints constrcl1 = constraintsMap.get(cl1);
					ClassLabConstraints constrcl2 = constraintsMap.get(cl2);
					
					int rankcl1 = 10000 * (constrcl1.getPartassign() != null ? 1 : 0)  
							+ 1000 * constrcl1.getUnmodifiableNotCompatible().size()
							+ 100 * constrcl1.getUnmodifiableUnwanted().size()
							+ 10 * constrcl1.getUnmodifiablePair().size()
							+ constrcl1.getPen_notInPreference() * (constrcl1.getPreference() != null ? 1 : 0);
					
					int rankcl2 = 10000 * (constrcl2.getPartassign() != null ? 1 : 0)  
							+ 1000 * constrcl2.getUnmodifiableNotCompatible().size()
							+ 100 * constrcl2.getUnmodifiableUnwanted().size()
							+ 10 * constrcl2.getUnmodifiablePair().size()
							+ constrcl2.getPen_notInPreference() * (constrcl2.getPreference() != null ? 1 : 0);
					
					return rankcl2 - rankcl1;
				});


		System.out.println("Done parsing file");
		if (debug) {
			System.out.println(stuffToBePlaced);
			System.out.println(slots);
			System.out.println(constraintsMap);
			
		}
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