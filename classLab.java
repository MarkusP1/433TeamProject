


public class classLab {
    private String faculty; //CPSC, SENG, etc.
    private int courseNumber;   //the 433 in CPSC 433
    private int courseSection;  //section 1, 2, etc., set to 0 if its a lab for every lecture section
    private boolean isLab;  //true if this is a lab
    private int labSection; //if its a lab, set to the correct lab section, set to 0 if this is a lecture

    public classLab(String inputFaculty, int inputCourseNumber, int inputCourseSection, boolean inputIsLab, int inputLabSection) {
        this.faculty = inputFaculty;
        this.courseNumber = inputCourseNumber;
        this.courseSection = inputCourseSection;
        this.isLab = inputIsLab;
        this.labSection = inputLabSection;
    }
}