

import java.util.ArrayList;
import java.util.Collections;
public class ClassLabConstraints {
	
	private ArrayList<ClassLab> notCompatible;
	private Slot partassign;
	private ArrayList<Slot> unwanted;
	private Slot preference;
	private int pen_notInPreference;
	private ArrayList<ClassLab> pair;
	
	public ClassLabConstraints() {
		
	}
	
	public ClassLabConstraints(ArrayList<ClassLab> notCompatible, Slot partassign,
			ArrayList<Slot> unwanted, Slot preference, int pen_notInPreference,
			ArrayList<ClassLab> pair) {
		this.notCompatible = new ArrayList<ClassLab>(notCompatible);
		this.partassign = partassign;
		this.unwanted = new ArrayList<Slot>(unwanted);
		this.preference = preference;
		this.pen_notInPreference = pen_notInPreference;
		this.pair = new ArrayList<ClassLab>(pair);
		
	}
	
	public boolean notCompatibleContains(ClassLab cl) {
		return notCompatible.contains(cl);
	}
	
	public void addNotCompatible(ClassLab cl) {
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
	
	public void addUnwanted(Slot sl) {
		unwanted.add(sl);
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
	
	public ArrayList<ClassLab> getUnmodifiablePair() {
		return (ArrayList<ClassLab>) Collections.unmodifiableList(pair);
	}
	
	public void addPair(ClassLab cl) {
		pair.add(cl);
	}

}
