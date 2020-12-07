

import java.lang.Math;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ConstraintChecker {
	
	/*HashMap<classLab, ArrayList<classLab>> notcompatibleMap;
	HashMap<classLab, Slot> partassignMap;
	HashMap<classLab, ArrayList<Slot>> unwantedMap;
	HashMap<classLab, ConstrPenPair<Slot>> preferenceMap;
	HashMap<classLab, classLab> pairMap;*/
	
	HashMap<classLab, classLabConstraints> constraintsMap;
	
	int pen_coursemin;
	int pen_labsmin;
	int pen_notpaired;
	int pen_section;

	boolean debug;
	
	public ConstraintChecker(HashMap<classLab, classLabConstraints> constraintsMap,
			int pen_coursemin, int pen_labsmin, int pen_notpaired, boolean debug) {
		this.constraintsMap = new HashMap<classLab, classLabConstraints>(constraintsMap);
		this.pen_coursemin = pen_coursemin;
		this.pen_labsmin = pen_labsmin;
		this.pen_notpaired = pen_notpaired;
		this.debug = debug;
	}
	
	private boolean constrMax(Prob pr) {
		for (Slot sl : pr.getUnmodifiableSlots()) {
			if (sl.getUnmodifiableclassLabs().size() > sl.getMax()) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean constrSectionLabs(Prob pr) {
		for (Slot sl1 : pr.getUnmodifiableSlots()) {
			ArrayList<Slot> conflictingSlots = new ArrayList<Slot>();
			
			for (Slot sl2 : pr.getUnmodifiableSlots()) {
				
				if (sl1.conflicts(sl2)) {
					conflictingSlots.add(sl2);
				}
			}
			
			for (classLab cl1 : sl1.getUnmodifiableclassLabs()) {
				
				if (!cl1.isLab()) {
					for (Slot sl2 : conflictingSlots) {
						for (classLab cl2 : sl2.getUnmodifiableclassLabs()) {
							
							if (cl1 != cl2 && cl2.isLab()
									&& cl1.getFaculty().equals(cl2.getFaculty())
									&& cl1.getCourseNumber().equals(cl2.getCourseNumber())
									&& cl1.getCourseSection() == cl2.getCourseSection()) {
								return false;
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private boolean constrNotCompatible(Prob pr) {
		for (Slot sl1 : pr.getUnmodifiableSlots()) {
			ArrayList<Slot> conflictingSlots = new ArrayList<Slot>();
			
			for (Slot sl2 : pr.getUnmodifiableSlots()) {
				
				if (sl1.conflicts(sl2)) {
					conflictingSlots.add(sl2);
				}
			}
			
			for (classLab cl1 : sl1.getUnmodifiableclassLabs()) {
				classLabConstraints constraints = constraintsMap.get(cl1);
				
				for (Slot sl2 : conflictingSlots) {
					for (classLab cl2 : sl2.getUnmodifiableclassLabs()) {

						if (cl1 != cl2 && constraints.notCompatibleContains(cl2)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}
	
	private boolean constrPartAssign(Prob pr) {
		for (Slot sl : pr.getUnmodifiableSlots()) {
			for (classLab cl : sl.getUnmodifiableclassLabs()) {
				classLabConstraints constraints = constraintsMap.get(cl);
				
				if (constraints.getPartassign() != null
						&& constraints.getPartassign() != sl) {
					return false;
				}
			}
		}

		return true;
	}
	
	private boolean constrUnwanted(Prob pr) {
		for (Slot sl : pr.getUnmodifiableSlots()) {
			for (classLab cl : sl.getUnmodifiableclassLabs()) {
				classLabConstraints constraints = constraintsMap.get(cl);
				
				if (constraints.unwantedContains(sl)) {
					return false;
				}
			}
		}

		return true;
	}
	
	private boolean constrEvening(Prob pr) {
		for (Slot sl : pr.getUnmodifiableSlots()) {
			
			if (sl.getStartTime().compareTo(LocalTime.of(18, 0)) < 0) {
				for (classLab cl : sl.getUnmodifiableclassLabs()) {
					
					if (!cl.isLab() && cl.getCourseSection() >= 9) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private boolean constr500Level(Prob pr) {
		for (Slot sl : pr.getUnmodifiableSlots()) {
			for (classLab cl1 : sl.getUnmodifiableclassLabs()) {
				
				if (!cl1.isLab() && cl1.getCourseNumber().charAt(0) == '5') {
					for (classLab cl2 : sl.getUnmodifiableclassLabs()) {
						
						if (cl1 != cl2 && cl2.getCourseNumber().charAt(0) == '5') {
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private int evalMinFilled(Prob pr) {
		int eval = 0;
		
		for (Slot sl : pr.getUnmodifiableSlots()) {
			if (sl.getUnmodifiableclassLabs().size() < sl.getMin()) {
				if (!sl.isLab()) {
					eval += Math.max(sl.getMin() - sl.getUnmodifiableclassLabs().size(), 0) * pen_coursemin;
				} else {
					eval += Math.max(sl.getMin() - sl.getUnmodifiableclassLabs().size(), 0) * pen_labsmin;
				}
			}
		}
		
		return eval;
	}
	
	private int evalPref(Prob pr) {
		int eval = 0;
		
		for (Slot sl : pr.getUnmodifiableSlots()) {
			for (classLab cl : sl.getUnmodifiableclassLabs()) {
				classLabConstraints constraints = constraintsMap.get(cl);
				Slot preference = constraints.getPreference();
				
				if (preference != null && preference != sl) {
					eval += constraints.getPen_notInPreference();
				}
			}
		}

		return eval;
	}
	
	private int evalPair(Prob pr) {
		int eval = 0;

		for (Slot sl1 : pr.getUnmodifiableSlots()) {
			ArrayList<Slot> conflictingSlots = new ArrayList<Slot>();
			
			for (Slot sl2 : pr.getUnmodifiableSlots()) {
				
				if (sl1.conflicts(sl2)) {
					conflictingSlots.add(sl2);
				}
			}
			
			for (classLab cl1 : sl1.getUnmodifiableclassLabs()) {
				classLabConstraints constraints = constraintsMap.get(cl1);
				ArrayList<classLab> pair = constraints.getUnmodifiablePair();
				
				for (Slot sl2 : conflictingSlots) {
					for (classLab cl2 : pair) {
						
						if (!sl2.getUnmodifiableclassLabs().contains(cl2)) {
							eval += pen_notpaired;
						}
					}
				}
			}
		}
		
		/*for (Slot sl : pr.getUnmodifiableSlots()) {
			ArrayList<classLab> classes = sl.getUnmodifiableclassLabs();
			
			for (classLab cl1 : classes) {
				classLabConstraints constraints = constraintsMap.get(cl1);
				ArrayList<classLab> pair = constraints.getUnmodifiablePair();
				
				for (classLab cl2 : pair) {
					
					if (!classes.contains(cl2)) {
						eval += pen_notpaired;
					}
				}
			}
		}*/
		
		return eval;
	}
	
	private int evalSecdiff(Prob pr) {
		int eval = 0;
		
		for (Slot sl: pr.getUnmodifiableSlots()) {
			ArrayList<classLab> classes = sl.getUnmodifiableclassLabs();
			
			for (int i = 0; i < classes.size() - 1; i++) {
				classLab cl1 = classes.get(i);
				
				if (!cl1.isLab()) {
					for (classLab cl2 : classes.subList(i + 1, classes.size())) {
						
						if (cl1.getFaculty().equals(cl2.getFaculty())
								&& cl1.getCourseNumber().equals(cl2.getCourseNumber())
								&& cl1.getCourseSection() != cl2.getCourseSection()) {
							eval += pen_section;
						}
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