package search;

import java.lang.Math;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ConstraintChecker {
	
	/*HashMap<UniClass, ArrayList<UniClass>> notcompatibleMap;
	HashMap<UniClass, Slot> partassignMap;
	HashMap<UniClass, ArrayList<Slot>> unwantedMap;
	HashMap<UniClass, ConstrPenPair<Slot>> preferenceMap;
	HashMap<UniClass, UniClass> pairMap;*/
	
	HashMap<UniClass, UniClassConstraints> constraintsMap;
	
	int pen_coursemin;
	int pen_labsmin;
	int pen_notpaired;
	int pen_section;

	
	public ConstraintChecker(HashMap<UniClass, UniClassConstraints> constraintsMap,
			int pen_coursemin, int pen_labsmin, int pen_notpaired) {
		this.constraintsMap = new HashMap<UniClass, UniClassConstraints>(constraintsMap);
		this.pen_coursemin = pen_coursemin;
		this.pen_labsmin = pen_labsmin;
		this.pen_notpaired = pen_notpaired;
	}
	
	private boolean constrMax(Prob pr) {
		for (Slot sl : pr.getSlots()) {
			if (sl.getClasses().size() > sl.getMax()) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean constrSectionLabs(Prob pr) {
		for (Slot sl : pr.getSlots()) {
			for (UniClass cl1 : sl.getClasses()) {
				for (UniClass cl2 : sl.getClasses()) {
					
					if (cl1 != cl2 && cl1.getIsCourse() && !cl2.getIsCourse()
							&& cl1.getFaculty().equals(cl2.getFaculty())
							&& cl1.getCourseNumber().equals(cl2.getCourseNumber())
							&& (cl2.getSectionNumber() == null
								|| cl1.getSectionNumber().equals(cl2.getSectionNumber()))) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/*private boolean constrSectionLabs(UniClass cl, UniClass cl2) {
		return !(cl.getIsCourse() && !cl2.getIsCourse()
			&& cl.getFaculty().equals(cl2.getFaculty())
			&& cl.getCourseNumber().equals(cl2.getCourseNumber())
			&& (cl2.getSectionNumber() == null
				|| cl.getSectionNumber().equals(cl2.getSectionNumber())));
	}*/
	
	private boolean constrNotCompatible(Prob pr) {
		for (Slot sl : pr.getSlots()) {
			for (UniClass cl1 : sl.getClasses()) {
				UniClassConstraints constraints = constraintsMap.get(cl1);

				for (UniClass cl2 : sl.getClasses()) {
					if (cl1 != cl2 && constraints.notCompatibleContains(cl2)) {
						return false;
					}
				}
			}
		}

		return true;
	}
	
	/*private boolean constrNotCompatible(UniClass cl1, UniClass cl2) {
		UniClassConstraints constraints = constraintsMap.get(cl1);
		
		return constraints == null || !constraints.notCompatibleContains(cl2);
	}*/
	
	private boolean constrPartAssign(Prob pr) {
		for (Slot sl : pr.getSlots()) {
			for (UniClass cl : sl.getClasses()) {
				UniClassConstraints constraints = constraintsMap.get(cl);
				
				if (constraints.getPartassign() != null
						&& constraints.getPartassign() != sl) {
					return false;
				}
			}
		}

		return true;
	}
	
	/*private boolean constrPartAssign(UniClass cl, Slot sl) {
		UniClassConstraints constraints = constraintsMap.get(cl);
		
		return constraints == null || constraints.getPartassign() == null 
				|| constraints.getPartassign() == sl;  
	}*/
	
	private boolean constrUnwanted(Prob pr) {
		for (Slot sl : pr.getSlots()) {
			for (UniClass cl : sl.getClasses()) {
				UniClassConstraints constraints = constraintsMap.get(cl);
				
				if (constraints.unwantedContains(sl)) {
					return false;
				}
			}
		}

		return true;
	}
	
	/*private boolean constrUnwanted(UniClass cl, Slot sl) {
		UniClassConstraints constraints = constraintsMap.get(cl);

		return constraints == null || !constraints.unwantedContains(sl);
	}*/
	
	private boolean constrEvening(Prob pr) {
		for (Slot sl : pr.getSlots()) {
			
			if (sl.getStartTime().compareTo(LocalTime.of(18, 0)) < 0) {
				for (UniClass cl : sl.getClasses()) {
					
					if (cl.getIsCourse() && cl.getSectionNumber().charAt(0) == '9') {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/*private boolean constrEvening(UniClass cl, Slot sl) {
		return !cl.getIsCourse() || cl.getSectionNumber() != 9
				 || sl.getStartTime().compareTo(LocalTime.of(18, 0)) >= 0;
	}*/
	
	private boolean constr500Level(Prob pr) {
		for (Slot sl : pr.getSlots()) {
			for (UniClass cl1 : sl.getClasses()) {
				
				if (cl1.getCourseNumber().charAt(0) == '5') {
					for (UniClass cl2 : sl.getClasses()) {
						
						if (cl1 != cl2 && cl2.getCourseNumber().charAt(0) == '5') {
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
	
	/*private boolean constr500Level(UniClass cl1, UniClass cl2) {
		return !(cl1.getCourseNumber()[0] == '5' && cl2.getCourseNumber()[0] == '5');
	}*/
	
	/*private boolean constr813Course(Prob pr) {
		for (Slot sl : pr.getSlots()) {
			for (UniClass cl1 : sl.getClasses()) {
				
				if (cl1.getCourseNumber().equals("813")) {
					if (!(sl.getStartTime().equals(LocalTime.of(18, 0)) 
						&& sl.getDuration().equals(Duration.ofHours(1)))) {
						return false;
					}
					
					for ()
				}
			}
		}
	}*/
	
	private int evalMinFilled(Prob pr) {
		int eval = 0;
		
		for (Slot sl : pr.getSlots()) {
			if (sl.getClasses().size() < sl.getMin()) {
				if (sl.getIsCourse()) {
					eval += Math.max(sl.getMin() - sl.getClasses().size(), 0) * pen_coursemin;
				} else {
					eval += Math.max(sl.getMin() - sl.getClasses().size(), 0) * pen_labsmin;
				}
			}
		}
		
		return eval;
	}
	
	/*private int evalMinFilled(Slot sl) {
		return max(sl.getMin() - sl.getUniClasses().size(), 0) * pen_labsmin;
	}*/
	
	private int evalPref(Prob pr) {
		int eval = 0;
		
		for (Slot sl : pr.getSlots()) {
			for (UniClass cl : sl.getClasses()) {
				UniClassConstraints constraints = constraintsMap.get(cl);
				Slot preference = constraints.getPreference();
				
				if (preference != null && preference != sl) {
					eval += constraints.getPen_notInPreference();
				}
			}
		}

		return eval;
	}
	
	/*private int evalPreference(UniClass cl, Slot sl) {
		UniClassConstraints constraints = constraintsMap.get(cl);
		
		if (constraints != null) {
			Slot preference = constraints.getPreference();
			
			if (preference != null && preference != sl) {
				return constraints.getPen_notInPreference();
			}
		}
		
		return 0;
	}*/
	
	private int evalPair(Prob pr) {
		int eval = 0;
		
		for (Slot sl : pr.getSlots()) {
			ArrayList<UniClass> classes = sl.getClasses();
			
			for (UniClass cl1 : classes) {
				UniClassConstraints constraints = constraintsMap.get(cl1);
				ArrayList<UniClass> pair = constraints.getPair();
				
				for (UniClass cl2 : pair) {
					
					if (!classes.contains(cl2)) {
						eval += pen_notpaired;
					}
				}
			}
		}
		
		return eval;
	}
	
	/*private int evalPair(UniClass cl1, ArrayList<UniClass> cl2) {
		UniClassConstraints constraints = constraintsMap.get(cl);


	}*/
	
	private int evalSecdiff(Prob pr) {
		int eval = 0;
		
		for (Slot sl: pr.getSlots()) {
			ArrayList<UniClass> classes = sl.getClasses();
			
			for (int i = 0; i < classes.size() - 1; i++) {
				UniClass cl1 = classes.get(i);
				for (UniClass cl2 : classes.subList(i + 1, classes.size())) {
					
					if (cl1.getFaculty().equals(cl2.getFaculty())
							&& cl1.getCourseNumber().equals(cl2.getCourseNumber())
							&& !cl1.getSectionNumber().equals(cl2.getSectionNumber())) {
						eval += pen_section;
					}
				}
			}
		}
		
		return eval;
	}
	
	public void constrStar(Prob pr) {
		boolean hardConstrs = constrMax(pr) && constrSectionLabs(pr) 
				&& constrNotCompatible(pr) && constrPartAssign(pr) && constrUnwanted(pr)
				&& constrEvening(pr) && constr500Level(pr);
		
		pr.setConstr(hardConstrs);
	}
	
	public void evalStar(Prob pr) {
		int eval = 0;
		
		eval += evalMinFilled(pr);
		eval += evalPref(pr);
		eval += evalPair(pr);
		eval += evalSecdiff(pr);
		
		pr.setEval(eval);
	}
}
