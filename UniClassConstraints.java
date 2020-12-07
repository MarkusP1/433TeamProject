

import java.util.ArrayList;
import java.util.Collections;
public class UniClassConstraints {
	
	private ArrayList<UniClass> notCompatible;
	private Slot partassign;
	private ArrayList<Slot> unwanted;
	private Slot preference;
	private int pen_notInPreference;
	private ArrayList<UniClass> pair;
	
	public UniClassConstraints() {
		
	}
	
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
	
	public void addNotCompatible(UniClass cl) {
		notCompatible.add(cl);
	}

	public Slot getPartassign() {
		return partassign;
	}
	
	public void setPartassign(Slot partassign) {
		this.partassign = partassign;
	}

	public boolean unwantedContains(Slot sl) {
		return unwanted.contains(sl);
	}
	
	public Slot getPreference() {
		return preference;
	}

	public void setPreference(Slot preference) {
		this.preference = preference;
	}
	
	public int getPen_notInPreference() {
		return pen_notInPreference;
	}

	public void setPen_notInPreference(int pen_notInPreference) {
		this.pen_notInPreference = pen_notInPreference;
	}
	
	public ArrayList<UniClass> getUnmodifiablePair() {
		return (ArrayList<UniClass>) Collections.unmodifiableList(pair);
	}

}
