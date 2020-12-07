import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

public class Slot {
	
    private ArrayList<DayOfWeek> days;    //1 for monday, 2 for tuesday, etc.
    private LocalTime startTime;   //24 hour clock, 1-24
    private LocalTime endTime;
    private int max;  // coursemax or labmax for the slot if this is a lab
    private int min;  // see above
    private boolean isLab;  //true if this is a lab
    
    private ArrayList<UniClass> uniClasses = new ArrayList<UniClass>();
    
	public Slot(ArrayList<DayOfWeek> days, LocalTime startTime, LocalTime endTime, int max, int min,
			boolean isLab) {
		this.days = new ArrayList<DayOfWeek>(days);
		this.startTime = startTime;
		this.endTime = endTime;
		this.max = max;
		this.min = min;
		this.isLab = isLab;
	}
	
	public ArrayList<DayOfWeek> getDays() {
		return new ArrayList<DayOfWeek>(days);
	}
	public LocalTime getStartTime() {
		return startTime;
	}
	public LocalTime getEndTime() {
		return endTime;
	}
	public int getMax() {
		return max;
	}
	public int getMin() {
		return min;
	}
	public boolean isLab() {
		return isLab;
	}
	public void addUniClass(UniClass cl) {
		uniClasses.add(cl);
	}
	public ArrayList<UniClass> getUnmodifiableUniClasses() {
		return (ArrayList<UniClass>) Collections.unmodifiableList(uniClasses);
	}

    public boolean conflicts(Slot sl) {
    	boolean daysConflict = this.days.stream()
    			.anyMatch(d -> sl.days.contains(d));
    	boolean timesConflict = this.startTime.isAfter(sl.endTime) 
    			|| this.endTime.isBefore(sl.startTime);
    	
    	return daysConflict && timesConflict;
    }
}