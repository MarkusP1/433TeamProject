import java.util.LinkedList;
import java.util.stream.Collectors;



public class State{
	public interface SolutionWriter {
		void writeSolution(Leaf l);
	}
	
	SolutionWriter writer;
	
	int bestsol;//This keeps track of the best known solution's eval*. 
	Leaf bestLeaf;
	
	LinkedList<Leaf> leaves = new LinkedList<Leaf>();//The list of leaves
	
	Leaf selected;//This is the leaf that will be operated on
	int selectedIndex;//This is the above leaf's index.
	
	// ConstraintChecker c;//This is the ConstraintChecker that goes with the intial situation.
	// Moved to AndTreeSearch
	
	boolean allSolved = false;//If there are any unsolved nodes, this is false, true otherwise.
	
	private boolean debug;
	
	State(LinkedList<Leaf> leaves, boolean debug, SolutionWriter writer /*,ConstraintChecker c*/){//Constructor
		this.leaves = leaves;
		this.bestsol = -1;
		//this.c = c;
		this.writer = writer;
		this.debug = debug;
	}


	void erw(ConstraintChecker c){
		if (debug)
			System.out.println(leaves.toString());
		if(allSolved){
		System.out.println("stillborn");
		}
		
		fleaf();//Changes 'selected' and its index to follow fleaf as outlined in the paper
		if (debug) {
			System.out.println("SELECTED NODE");
			System.out.println(selected.toString());
			System.out.println("SELECTED NODE");
		}
		
		solved(selectedIndex);//Checks if the given leaf is solved, does operations on it contingent on that. The name does not indicate the true purpose of this function!
		
		if(selectedIndex != -1){//If there is a selected index, perform a transition
			ftrans(c);
		}
		
		
	}
	
	boolean goal(int limit){//The goal is reached if there are no leaves (no solution found) or all the leafs are potential solutions. originally included this condition: (leaves.size() == 1 && leaves.get(0).sol == 'y')||
		allSolved = true;
		for(int i = 0; i<leaves.size(); i++){
			 if(leaves.get(i).sol == '?'){
				 allSolved = false;
			 }
		 }
		 
		 if(leaves.size() == 0||allSolved){
			return true; 
		 }
		else{
			return false;
		}

	}



	
	
	void solved(int i){//This takes the index of the thing to be evaluated and does a buch of different stuff.
		Leaf l = leaves.get(i);
	
		if(l.pr.stuffToBePlaced.size() == 0 && l.constr){//If the node is solved and the solution fits the constraints...
			l.setSol('y');
			if (debug)
				System.out.println("NODE SOLVED");
			
			if(l.eval < bestsol || bestsol == -1){
				bestLeaf = l;
				bestsol = l.eval;
				System.out.println("New best solution found with eval-value: " + bestsol);
				writer.writeSolution(bestLeaf);
				//bestIndex = i;
			}
			selectedIndex = -1;//SelectedIndex = -1 means 'don't do any transitions on this leaf'
			
		}
		if(!l.constr
				|| (bestsol != -1 && l.fbound > bestsol)){//If the node's problem either violates some constraints or is has a worse eval than the best one...
			if(l.constr && debug){
				System.out.println("NODE DELETED JERK");
			}
			
			leaves.remove(i);///..get rid of it
			if (debug)
				System.out.println("NODE DELETED");
			selectedIndex = -1;//and make sure no transition will be attempted this pass.
			//if(bestIndex > i){
			//	bestIndex = bestIndex -1;
			//}
		}
		//If none of the above apply, the leaf at i just retains is '?' sol value.
	}
	
	
	
	
	LinkedList<Prob> div(Prob p)
    {
        if (p.stuffToBePlaced.size() ==  0)
            return null;
        else{
            Prob labChild = new Prob(p);
            ClassLab beingAdded = labChild.stuffToBePlaced.remove(0);
            return createChildren(labChild, beingAdded);

        }
    }
	
    LinkedList<Prob> createChildren(Prob labChild, ClassLab beingAdded)
    {
        LinkedList<Prob> children = new LinkedList<Prob>();

        for( int i = 0 ; i < labChild.courseSlots.size() ; i++)
        {
            //add beingAdded to the ith course slot then add it to the children
            //only if the item being added is a lab and the slot is a lab
            //or if the item being added is a not lab and the slot is not a lab
            if((labChild.courseSlots.get(i).isLab() && beingAdded.isLabOrTut()) ||(!labChild.courseSlots.get(i).isLab() && !beingAdded.isLabOrTut()) ) {
                Prob child = new Prob(labChild);
				
                child.courseSlots.get(i).addClassLab(beingAdded);
                children.add(child);
				if (debug) {
    				System.out.println("CHILD");
    				System.out.println(child.toString());
                }
            }

        }
        return children;
    }
	
	
	void ftrans(ConstraintChecker c){
		LinkedList<Prob> children = div(leaves.get(selectedIndex).pr);//Making a linked list of child nodes
		
		//LinkedList<Leaf> newLeaves = new LinkedList<Leaf>();
		
		int parentdepth = leaves.get(selectedIndex).depth;
	

		LinkedList<Leaf> newLeaves = children.stream()
				.map(pr -> {
					Leaf l = new Leaf(pr, parentdepth + 1);
					l.setEval(c);
					l.setConstr(c);
					
					return l;
				}).filter(l -> l.constr)
				.collect(Collectors.toCollection(LinkedList<Leaf>::new));
		
		/*for(int i = 0; i<selectedIndex;i++){//Adding all the leaves that come before the selected one
			newLeaves.add(leaves.get(i));
		}
		for(int i = 0; i<children.size();i++){//Adding all a bunch of leaves containing the problems div gave
			Leaf l = new Leaf(children.get(i), parentdepth+1);
			l.setEval(c);
			l.setConstr(c);
			
			
			if(l.constr && (l.eval <= bestsol || bestsol == -1)){
				newLeaves.add(l);
				if (debug)
					System.out.println("Child added");
				
			}
			if(!l.constr && debug){
				System.out.println("Following child violated constraints");
				System.out.println(l.toString());
			}
			
			
		}
		if (debug)
			System.out.println(children.size());
		for(int i = selectedIndex+1; i<leaves.size();i++){//Adding all the leaves after the selected one
			newLeaves.add(leaves.get(i));
		}
		
		leaves = newLeaves;*/
		
		leaves.remove(selectedIndex);
		leaves.addAll(selectedIndex, newLeaves);
		
	}
	
	 void fleaf(){
		//I believe that this should work.
		boolean beenSelected = false;
		int deepest = 0;
		int besteval = -1;
		
		//These go through the linked list from the right.
		if(bestsol == -1){
			for (int i = 0; i < leaves.size(); i++) {
				Leaf l = leaves.get(leaves.size()-i-1);
				
				 if(l.sol == '?' && !beenSelected);{
					selected = l;
					beenSelected = true;
					deepest = l.depth;
					besteval = l.eval;
					selectedIndex = leaves.size()-i-1;
				 }
				 if(l.sol == '?' && l.depth > deepest){
					selected = l;
					deepest = l.depth;
					besteval = Math.min(besteval, l.eval);
					selectedIndex = leaves.size()-i-1;
					
					deepest = l.depth;
				 }
				 else if(l.sol == '?' && l.depth == deepest && l.eval < besteval){
					selected = l;
					besteval = l.eval;
					selectedIndex = leaves.size()-i-1;
					
					deepest = l.depth;
				 }
				  else if(l.sol == '?' && l.depth == deepest && l.eval == besteval){
					selected = l;
					selectedIndex = leaves.size()-i-1;
					
					besteval = l.eval;
					deepest = l.depth;
				 }
				 else{
					 allSolved = true;
				 }
			}
			
		}
		if(bestsol >= 0){
			 beenSelected = false;
			
			for (int i = 0; i < leaves.size(); i++) {//Finding all unsolved leaves
				Leaf l = leaves.get(leaves.size()-i-1);
			
				//System.out.println(beenSelected);
				
			
				if (l.sol == '?' && !beenSelected) {
					if (debug)
						System.out.println(!beenSelected);
					selected = l;
					beenSelected = true;
					deepest = l.depth;
					besteval = l.eval;
					selectedIndex = leaves.size()-i-1;
					if (debug)
						System.out.println(selectedIndex);
					
					
				}
				 
				if(l.sol == '?' && l.eval < besteval){
					selected = l;
					besteval = l.eval;
					selectedIndex = leaves.size()-i-1;
					deepest = l.depth;
				}
				else if(l.sol == '?' && l.eval == besteval && l.depth > deepest){
					selected = l;
					deepest = l.depth;
					//besteval = Math.min(besteval, leaves.get(leaves.size()-i-1).eval);
					selectedIndex = leaves.size()-i-1;
				}
				else if(l.sol == '?' && l.eval == besteval && l.depth == deepest){
					selected = l;
					selectedIndex = leaves.size()-i-1;
					//besteval = leaves.get(leaves.size()-i-1).eval;
					//deepest = leaves.get(leaves.size()-i-1).depth;
				}
				else{
					 allSolved = true;
 				}
				 
				// System.out.println(beenSelected);
			}
		//	System.out.println(selectedIndex);
			
		}

		
	}
	
	
	
	
}