


public class UniClass {
	
    private String faculty; //CPSC, SENG, etc.
    private String courseNumber;   //the 433 in CPSC 433
    private int courseSection;  //section 01, 02, etc., set to null if its a lab for every lecture section
    private boolean isLab;  //true if this is a lab
    private int labSection; //if its a lab, set to the correct lab section, set to 0 if this is a lecture
    
	public UniClass(String faculty, String courseNumber, int courseSection, boolean isLab, int labSection) {
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

    
}