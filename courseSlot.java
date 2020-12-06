import java.time.*;



public class courseSlot {
    private DayOfWeek day;    
    private LocalTime time;   
    private int courseMax;  //coursmax is also labmax for the slot if this is a lab
    private int courseMin;  //see above
    private boolean isLab;  //true if this is a lab

    public courseSlot(DayOfWeek inputDay, LocalTime inputTime, int inputCourseMax, int inputCourseMin, boolean inputIsLab) {
        this.day = inputDay;
        this.time = inputTime;
        this.courseMax = inputCourseMax;
        this.courseMin = inputCourseMin;
        this.isLab = inputIsLab;
    }
}