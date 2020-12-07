


public class ClassLab {
	
    private String faculty; //CPSC, SENG, etc.
    private String courseNumber;   //the 433 in CPSC 433
    private int courseSection;  //section 01, 02, etc., set to null if its a lab for every lecture section
    private boolean isLab;  //true if this is a lab
    private int labSection; //if its a lab, set to the correct lab section, set to 0 if this is a lecture
    
	public ClassLab(String faculty, String courseNumber, int courseSection, boolean isLab, int labSection) {
		this.faculty = new String(faculty);
		this.courseNumber = new String(courseNumber);
		this.courseSection = courseSection;
		this.isLab = isLab;
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

	public boolean isLab() {
		return isLab;
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
					&& (this.isLab == cl.isLab)
					&& this.labSection == cl.labSection;
		} else {
			return super.equals(obj);
		}
	}
    
    // @Overrides Object.toString()
    public String toString() {
    	String sectionDesc = "";
    	if (!(isLab && courseSection == 0)) {
    		sectionDesc = "LEC " + String.format("%1$02d", courseSection) + " ";
    	}
    	
    	if (isLab) {
    		sectionDesc = sectionDesc + "LAB " + labSection;
    	}
    	
    	return faculty + " " + courseNumber + " "
    			+ sectionDesc;
    }
    
}