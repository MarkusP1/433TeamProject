


public class UniClass {
	
    private String faculty; //CPSC, SENG, etc.
    private String courseNumber;   //the 433 in CPSC 433
    private String courseSection;  //section 01, 02, etc., set to null if its a lab for every lecture section
    private boolean isLab;  //true if this is a lab
    private int labSection; //if its a lab, set to the correct lab section, set to 0 if this is a lecture
    
	public UniClass(String faculty, String courseNumber, String courseSection, boolean isLab, int labSection) {
		this.faculty = faculty;
		this.courseNumber = courseNumber;
		this.courseSection = courseSection;
		this.isLab = isLab;
		this.labSection = labSection;
	}

	public String getFaculty() {
		return faculty;
	}

	public String getCourseNumber() {
		return courseNumber;
	}

	public String getCourseSection() {
		return courseSection;
	}

	public boolean isLab() {
		return isLab;
	}

	public int getLabSection() {
		return labSection;
	}

    
}