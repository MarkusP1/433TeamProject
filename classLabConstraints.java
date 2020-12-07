import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;


public class classLabConstraints {

	private ArrayList<classLab> notCompatible;
	private Slot partassign;
	private ArrayList<Slot> unwanted;
	private Slot preference;
	private int pen_notInPreference;
	private ArrayList<classLab> pair;

	public classLabConstraints(ArrayList<classLab> notCompatible, Slot partassign,
			ArrayList<Slot> unwanted, Slot preference, int pen_notInPreference,
			ArrayList<classLab> pair) {
		this.notCompatible = new ArrayList<classLab>(notCompatible);
		this.partassign = partassign;
		this.unwanted = new ArrayList<Slot>(unwanted);
		this.preference = preference;
		this.pen_notInPreference = pen_notInPreference;
		this.pair = new ArrayList<classLab>(pair);

	}

	public boolean notCompatibleContains(classLab cl) {
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

	//public ArrayList<classLab> getPair() {
	//	return pair;
	//}
	
	public ArrayList<classLab> getUnmodifiablePair() {
		return (ArrayList<classLab>) Collections.unmodifiableList(pair);
	}

}