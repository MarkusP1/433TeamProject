package search;

import java.util.ArrayList;
import java.util.function.Predicate;

public class UniClassConstraints {
	
	private ArrayList<UniClass> notCompatible;
	private Slot partassign;
	private ArrayList<Slot> unwanted;
	private Slot preference;
	private int pen_notInPreference;
	private ArrayList<UniClass> pair;
	
	public UniClassConstraints(ArrayList<UniClass> notCompatible, Slot partassign,
			ArrayList<Slot> unwanted, Slot preference, int pen_notInPreference,
			ArrayList<UniClass> pair) {
		this.notCompatible = new ArrayList<UniClass>(notCompatible);
		this.partassign = partassign;
		this.unwanted = new ArrayList<Slot>(unwanted);
		this.preference = preference;
		this.pen_notInPreference = pen_notInPreference;
		this.pair = new ArrayList<UniClass>(pair);
		
	}
	
	public boolean notCompatibleContains(UniClass cl) {
		return notCompatible.contains(cl);
	}

	public Slot getPartassign() {
		return partassign;
	}
	
	public boolean unwantedContains(Slot sl) {
		return unwanted.contains(sl);
	}
	
	public Slot getPreference() {
		return preference;
	}
	
	public int getPen_notInPreference() {
		return pen_notInPreference;
	}
	
	public ArrayList<UniClass> getPair() {
		return pair;
	}

}
