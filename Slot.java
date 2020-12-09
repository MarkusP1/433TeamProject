import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Slot {
	
    private ArrayList<DayOfWeek> days;    //1 for monday, 2 for tuesday, etc.
    private LocalTime startTime;   //24 hour clock, 1-24
    private LocalTime endTime;
    private int max;  // coursemax or labmax for the slot if this is a lab
    private int min;  // see above
    private boolean isLab;  // true if this is a lab
    
    private ArrayList<ClassLab> classes = new ArrayList<ClassLab>(); // list of classes assigned to this slot
    
    // full constructor, classes starts empty
	public Slot(ArrayList<DayOfWeek> days, LocalTime startTime, LocalTime endTime, int max, int min,
			boolean isLab) {
		this.days = new ArrayList<DayOfWeek>(days);
		this.startTime = startTime;
		this.endTime = endTime;
		this.max = max;
		this.min = min;
		this.isLab = isLab;
	}
	
	// copy constructor with shallow copy of classes (class references are used in comparison so no deep copying)
	public Slot(Slot copy) {
		this.days = new ArrayList<DayOfWeek>(copy.days);
		this.startTime = copy.startTime;
		this.endTime = copy.endTime;
		this.max = copy.max;
		this.min = copy.min;
		this.isLab = copy.isLab;
		this.classes = new ArrayList<ClassLab>(copy.classes);
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
	public void addClassLab(ClassLab cl) {
		classes.add(cl);
	}
	public List<ClassLab> getUnmodifiableClasses() {
		return Collections.unmodifiableList(classes);
	}

	// check if days conflict and times conflict
    public boolean conflicts(Slot sl) {
    	boolean daysConflict = this.days.stream()
    			.anyMatch(d -> sl.days.contains(d));
    	boolean timesConflict = ((this.startTime.isAfter(sl.startTime) || this.startTime.equals(sl.startTime)) 
    					&& this.startTime.isBefore(sl.endTime))
    			|| (this.endTime.isAfter(sl.startTime) 
    					&& (this.endTime.isBefore(sl.endTime) || this.endTime.equals(sl.endTime)))
    			|| (sl.startTime.isAfter(this.startTime) && sl.endTime.isBefore(this.endTime));
    	
    	return daysConflict && timesConflict;
    }
    
    // checks equality without checking min and max
    public boolean weakEquals(Slot sl) {
    	return (this.isLab == sl.isLab)
    			&& this.startTime.equals(sl.startTime)
    			&& this.endTime.equals(sl.endTime)
    			&& this.days.equals(sl.days);
    }

    // @Overrides Object.toString()
    public boolean equals(Object obj) {
		if (obj.getClass().equals(Slot.class)) {
			Slot sl = (Slot) obj;
			return weakEquals(sl)
					&& this.max == sl.max && this.min == sl.min;
		} else {
			return super.equals(obj);
		}
    }
    
    // @Overrides Object.toString()
    public String toString() {
    	String slotType = isLab ? "Lab" : "Lecture";
    	
    	return slotType + " slot on "
    			+ days.toString() + " from "
    			+ startTime.toString() + " to "
    			+ endTime.toString() + " of "
    			+ classes.toString();
    }
    
    public String toMinimalString() {
    	return days.get(0).toString().substring(0, 2) + ", " + startTime.toString();
    }
}