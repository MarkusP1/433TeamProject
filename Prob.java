import java.util.ArrayList;
import java.util.Collections;

public class Prob {
	boolean constr;
	int eval;
    ArrayList<ClassLab> stuffToBePlaced;
    ArrayList<Slot> courseSlots;

	
    public Prob(ArrayList<ClassLab> stuffToBePlaced, ArrayList<Slot> courseSlots) {
    	this.stuffToBePlaced = new ArrayList<ClassLab>(stuffToBePlaced);
    	this.courseSlots = new ArrayList<Slot>(courseSlots);
    }
	
    public Prob() {
    	this.stuffToBePlaced = new ArrayList<ClassLab>();
    	this.courseSlots = new ArrayList<Slot>();
    }
    
	public ArrayList<Slot> getUnmodifiableSlots() {
    	return (ArrayList<Slot>) Collections.unmodifiableList(courseSlots);
    }
	
    public void setConstr(boolean constr) {
    	this.constr = constr;
    }
    
    public void setEval(int eval) {
    	this.eval = eval;
    }
	
	public boolean getConstr() {
    	return constr;
    }
    
    public int getEval() {
    	return eval;
    }
	
    //may want variables for constr and eval
    //to set the arraylists use something like courseSlots = ReaderThing.getCourses();
    public Prob(Prob copy)
    {
        this.stuffToBePlaced =copy.stuffToBePlaced;
        this.courseSlots = copy.courseSlots;
    }

}