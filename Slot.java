import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

public class Slot {
	
    private ArrayList<DayOfWeek> days;    //1 for monday, 2 for tuesday, etc.
    private LocalTime startTime;   //24 hour clock, 1-24
    private LocalTime endTime;
    private int max;  // coursemax or labmax for the slot if this is a lab
    private int min;  // see above
    private boolean isLab;  //true if this is a lab
    
	public Slot(ArrayList<DayOfWeek> days, LocalTime startTime, LocalTime endTime, int max, int min,
			boolean isLab) {
		this.days = days;
		this.startTime = startTime;
		this.endTime = endTime;
		this.max = max;
		this.min = min;
		this.isLab = isLab;
	}
	
	public ArrayList<DayOfWeek> getDays() {
		return days;
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

    
}