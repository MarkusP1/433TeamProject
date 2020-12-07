public class classLab {

    private int courseNumber;   //the 433 in CPSC 433
    private String faculty; //CPSC, SENG, etc.
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








    //GETTERS AND SETTERS
    public String getFaculty() {
        return faculty;
    }
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
    public int getCourseNumber() {
        return courseNumber;
    }
    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }
    public int getCourseSection() {
        return courseSection;
    }
    public void setCourseSection(int courseSection) {
        this.courseSection = courseSection;
    }
    public boolean getIsLab() {
        return isLab;
    }
    public void setIsLab(boolean lab) {
        isLab = lab;
    }
    public int getLabSection() {
        return labSection;
    }
    public void setLabSection(int labSection) {
        this.labSection = labSection;
    }

}
