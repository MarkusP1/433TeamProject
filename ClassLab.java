


public class ClassLab {
	
    private String faculty; //CPSC, SENG, etc.
    private String courseNumber;   //the 433 in CPSC 433
    private int courseSection;  //section 01, 02, etc., set to null if its a lab for every lecture section
    private boolean isLabOrTut;  //true if this is a lab
    private boolean isTut;
    private int labSection; //if its a lab, set to the correct lab section, set to 0 if this is a lecture
    
    // full constructor
	public ClassLab(String faculty, String courseNumber, int courseSection, boolean isLabOrTut, boolean isTut, int labSection) {
		this.faculty = new String(faculty);
		this.courseNumber = new String(courseNumber);
		this.courseSection = courseSection;
		this.isLabOrTut = isLabOrTut;
		this.isTut = isTut;
		this.labSection = labSection;
	}

	public String getFaculty() {
		return new String(faculty);
	}

	public String getCourseNumber() {
		return new String(courseNumber);
	}

	public int getCourseSection() {
		return courseSection;
	}

	public boolean isLabOrTut() {
		return isLabOrTut;
	}
	
	public boolean isTut() {
		return isTut;
	}

	public int getLabSection() {
		return labSection;
	}

    // @Overrides Object.equals(Object obj)
	public boolean equals(Object obj) {
		if (obj.getClass().equals(ClassLab.class)) {
			ClassLab cl = (ClassLab) obj;
			return this.faculty.equals(cl.faculty)
					&& this.courseNumber.equals(cl.courseNumber)
					&& this.courseSection == cl.courseSection
					&& (this.isLabOrTut == cl.isLabOrTut)
					&& this.labSection == cl.labSection;
		} else {
			return super.equals(obj);
		}
	}
    
    // @Overrides Object.toString()
    public String toString() {
    	String sectionDesc = "";
    	if (!(isLabOrTut && courseSection == 0)) {
    		sectionDesc = "LEC " + String.format("%1$02d", courseSection) + " ";
    	}
    	
    	if (isLabOrTut) {
    		String labOrTutID = isTut ? "TUT " : "LAB ";
    		
    		sectionDesc = sectionDesc + labOrTutID + String.format("%1$02d", labSection);
    	}
    	
    	return faculty + " " + courseNumber + " "
    			+ sectionDesc;
    }
    
}