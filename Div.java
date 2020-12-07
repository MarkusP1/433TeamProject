import java.util.ArrayList;

public class Div {
    public ArrayList<Prob> div(Prob p)
    {
        if (p.stuffToBePlaced.size() ==  0)
            return null;
        else{
            Prob labChild = new Prob(p);
            classLab beingAdded = labChild.stuffToBePlaced.remove(0);
            return createChildren(labChild, beingAdded);

        }
    }
    public ArrayList<Prob> createChildren(Prob labChild, classLab beingAdded)
    {
        ArrayList<Prob> children = new ArrayList<Prob>();

        for( int i = 0 ; i < labChild.courseSlots.size() ; i++)
        {
            //add beingAdded to the ith course slot then add it to the children
            //only if the item being added is a lab and the slot is a lab
            //or if the item being added is a not lab and the slot is not a lab
            if((labChild.courseSlots.get(i).isLab() && beingAdded.getIsLab()) ||(!labChild.courseSlots.get(i).isLab() && !beingAdded.getIsLab()) ) {
                Prob child = new Prob(labChild);
                labChild.courseSlots.get(i).classes.add(beingAdded);
                children.add(child);
            }

        }

        return children;
    }

}
