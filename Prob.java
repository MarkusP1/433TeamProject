import java.util.ArrayList;
import java.util.Collections;



public class Prob{
    ArrayList<ClassLab> stuffToBePlaced = new ArrayList<ClassLab>();
    ArrayList<Slot> slots = new ArrayList<Slot>();
    
    public ArrayList<Slot> getUnmodifiableSlots() {
    	return (ArrayList<Slot>) Collections.unmodifiableList(slots);
    }
    
    public void setConstr(boolean constr) {
    	
    }
    
    public void setEval(int eval) {
    	
    }
    //may want variables for constr and eval
    //to set the arraylists use something like courseSlots = ReaderThing.getCourses();

}