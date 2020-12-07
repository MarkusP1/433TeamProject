import java.util.ArrayList;


public class Prob{
    ArrayList<classLab> stuffToBePlaced = new ArrayList<classLab>();
    ArrayList<Slot> courseSlots = new ArrayList<Slot>();
    public Prob()
    {
    }
    //may want variables for constr and eval
    //to set the arraylists use something like courseSlots = ReaderThing.getCourses();
    public Prob(Prob copy)
    {
        this.stuffToBePlaced =copy.stuffToBePlaced;
        this.courseSlots = copy.courseSlots;
    }

}