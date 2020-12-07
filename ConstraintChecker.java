

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
	
	HashMap<ClassLab, ClassLabConstraints> constraintsMap;
	
	int pen_coursemin;
	int pen_labsmin;
	int pen_notpaired;
	int pen_section;
	
	float w_minfilled;
	float w_pref;
	float w_pair;
	float w_secdiff;

	boolean debug;
	
	public ConstraintChecker(HashMap<ClassLab, ClassLabConstraints> constraintsMap,
			int pen_coursemin, int pen_labsmin, int pen_notpaired, int pen_section,
			float w_minfilled, float w_pref, float w_pair, float w_secdiff, boolean debug) {
		this.constraintsMap = new HashMap<ClassLab, ClassLabConstraints>(constraintsMap);
		this.pen_coursemin = pen_coursemin;
		this.pen_labsmin = pen_labsmin;
		this.pen_notpaired = pen_notpaired;
		this.pen_section = pen_section;
		this.w_minfilled = w_minfilled;
		this.w_pref = w_pref;
		this.w_pair = w_pair;
		this.w_secdiff = w_secdiff;
		this.debug = debug;
	}
	
	private boolean constrMax(Prob pr) {
		for (Slot sl : pr.getUnmodifiableSlots()) {
			if (sl.getUnmodifiableClasses().size() > sl.getMax()) {
				if (debug) {
					System.out.println("max constraint violated by:\n" + sl);
				}
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
			
			for (ClassLab cl1 : sl1.getUnmodifiableClasses()) {
				
				if (!cl1.isLab()) {
					for (Slot sl2 : conflictingSlots) {
						for (ClassLab cl2 : sl2.getUnmodifiableClasses()) {
							
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
			
			for (ClassLab cl1 : sl1.getUnmodifiableClasses()) {
				ClassLabConstraints constraints = constraintsMap.get(cl1);
				
				for (Slot sl2 : conflictingSlots) {
					for (ClassLab cl2 : sl2.getUnmodifiableClasses()) {

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
			for (ClassLab cl : sl.getUnmodifiableClasses()) {
				ClassLabConstraints constraints = constraintsMap.get(cl);
				
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
			for (ClassLab cl : sl.getUnmodifiableClasses()) {
				ClassLabConstraints constraints = constraintsMap.get(cl);
				
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
				for (ClassLab cl : sl.getUnmodifiableClasses()) {
					
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
			for (ClassLab cl1 : sl.getUnmodifiableClasses()) {
				
				if (!cl1.isLab() && cl1.getCourseNumber().charAt(0) == '5') {
					for (ClassLab cl2 : sl.getUnmodifiableClasses()) {
						
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
			if (sl.getUnmodifiableClasses().size() < sl.getMin()) {
				if (!sl.isLab()) {
					eval += Math.max(sl.getMin() - sl.getUnmodifiableClasses().size(), 0) * pen_coursemin;
				} else {
					eval += Math.max(sl.getMin() - sl.getUnmodifiableClasses().size(), 0) * pen_labsmin;
				}
			}
		}
		
		return eval;
	}
	
	private int evalPref(Prob pr) {
		int eval = 0;
		
		for (Slot sl : pr.getUnmodifiableSlots()) {
			for (ClassLab cl : sl.getUnmodifiableClasses()) {
				ClassLabConstraints constraints = constraintsMap.get(cl);
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
			
			for (ClassLab cl1 : sl1.getUnmodifiableClasses()) {
				ClassLabConstraints constraints = constraintsMap.get(cl1);
				ArrayList<ClassLab> pair = constraints.getUnmodifiablePair();
				
				for (Slot sl2 : conflictingSlots) {
					for (ClassLab cl2 : pair) {
						
						if (!sl2.getUnmodifiableClasses().contains(cl2)) {
							eval += pen_notpaired;
						}
					}
				}
			}
		}
		
		/*for (Slot sl : pr.getUnmodifiableSlots()) {
			ArrayList<UniClass> classes = sl.getUnmodifiableUniClasses();
			
			for (UniClass cl1 : classes) {
				UniClassConstraints constraints = constraintsMap.get(cl1);
				ArrayList<UniClass> pair = constraints.getUnmodifiablePair();
				
				for (UniClass cl2 : pair) {
					
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
			ArrayList<ClassLab> classes = sl.getUnmodifiableClasses();
			
			for (int i = 0; i < classes.size() - 1; i++) {
				ClassLab cl1 = classes.get(i);
				
				if (!cl1.isLab()) {
					for (ClassLab cl2 : classes.subList(i + 1, classes.size())) {
						
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
		
		eval += evalMinFilled(pr) * w_minfilled;
		eval += evalPref(pr) * w_pref;
		eval += evalPair(pr) * w_pair;
		eval += evalSecdiff(pr) * w_secdiff;
		
		pr.setEval(eval);
	}
}
