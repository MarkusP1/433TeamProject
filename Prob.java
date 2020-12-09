import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prob {
	boolean constr;
	int eval;
	int fbound; 
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
    
	public List<Slot> getUnmodifiableSlots() {
    	return Collections.unmodifiableList(courseSlots);
    }
	
    public void setConstr(boolean constr) {
    	this.constr = constr;
    }
    
    public void setEval(int eval) {
    	this.eval = eval;
    }
	
	
	public void setFbound(int eval) {
    	this.fbound = eval;
    }
	public int getFbound() {
    	return fbound;
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
        this.stuffToBePlaced = new ArrayList<ClassLab>(copy.stuffToBePlaced);
        this.courseSlots = new ArrayList<Slot>();
        for (Slot sl : copy.courseSlots) {
        	this.courseSlots.add(new Slot(sl));
        }
    }

    public String toString() {
    	return "Prob with\n"
    			+ stuffToBePlaced.toString() + "\n"
    			+ courseSlots.toString();
    }
    
}