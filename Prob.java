import java.util.ArrayList;
import java.util.Collections;



public class Prob{
    ArrayList<UniClass> stuffToBePlaced = new ArrayList<UniClass>();
    ArrayList<Slot> slots = new ArrayList<Slot>();
    
    public ArrayList<Slot> getUnmodifiableSlots() {
    	return (ArrayList<Slot>) Collections.unmodifiableList(slots);
    }
    
    public void setConstr(boolean constr) {
    	
    }
    
    public void setEval(int eval) {
    	
    }
}