import java.util.LinkedList;
import java.util.stream.Collectors;



public class State{
	
	public interface SolutionWriter {
		void writeSolution(Leaf l);
	}
	
	SolutionWriter writer;
	
	int bestsol;//This keeps track of the best known solution's eval. 
	Leaf bestLeaf;//This stores the leaf whose prob is both solved and has the best known eval out of all the solved nodes.
	
	
	//State uses a list of leaf nodes to represent the and tree.
	// We don't need to store inner nodes because we use a branch-and-bound model with no backtracking.
	LinkedList<Leaf> leaves = new LinkedList<Leaf>();//This is the list of leaves.
	
	Leaf selected;//This is the leaf that ftrans will operate on when called
	int selectedIndex;//This is the above leaf's index.
	

	//Note that an unsolved node is one whose problem fulfills all hard constraints and has no courses to be placed.
	//An unsolved node is a node that is not solved.
	boolean allSolved = false;//If there are any unsolved nodes, this is false, true otherwise.
	
	private boolean debug;//This controls whether or not various statements for debugging are printed
	
	
	
	State(LinkedList<Leaf> leaves, boolean debug, SolutionWriter writer /*,ConstraintChecker c*/){
		this.leaves = leaves;
		this.bestsol = -1;//bestsol starts at -1 to indicate no solution has been found.
		//this.c = c;
		this.writer = writer;
		this.debug = debug;
	}


	void erw(ConstraintChecker c){//This function grows the tree.
		if (debug)
			System.out.println(leaves.toString());
		if(allSolved){
		System.out.println("ALL SOLVED");
		}
		
		fleaf();//This function changes 'selectedIndex' to find the leaf to be operated on
		
		if (debug) {
			System.out.println("SELECTED NODE");
			System.out.println(selected.toString());
			System.out.println("SELECTED NODE");
		}
		
		solved(selectedIndex);//This function checks if the given leaf is solved and does operations on it contingent on that. See the comments on 'solved' for more details. 
		
		if(selectedIndex != -1){//If there is a selected index (which is indicated by a value not equal to -1)... 
			ftrans(c);//...perform a transition
		}
		
		
	}
	
	boolean goal(){//The goal is reached if there are no leaves (no solution found) or all the leafs are solved (meaning that the best must be somwhere among them);
	
		//First, we see if all nodes are solved by looping through them all and checking their sol-values. 
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



	
	
	void solved(int i){//This takes the index a node, checks if it's solved and depending on its 'solution status' (solved, or unknown) along with its constr, eval and fbound 
					   //does different operations. 
					   
		Leaf l = leaves.get(i);
	
		if(l.pr.stuffToBePlaced.size() == 0 && l.constr){//If the node is solved and the solution fits the constraints...
			l.setSol('y');//Mark it as solved
			if (debug)
				System.out.println("NODE SOLVED");
			
			if(l.eval < bestsol || bestsol == -1){//If the solution has a better fbound than the best or no solution has been found yet...
				bestLeaf = l;//...change the best node to the given node.
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
				System.out.println("NODE DELETED DUE TO CONSTR");
			}
			
			leaves.remove(i);//..get rid of it
			if (debug)
				System.out.println("NODE DELETED");
			selectedIndex = -1;//and make sure no transition will be attempted this pass.
			//if(bestIndex > i){
			//	bestIndex = bestIndex -1;
			//}
		}
		//If none of the above apply, the leaf at i just retains is '?' sol value.
	}
	
	
	
	
	LinkedList<Prob> div(Prob p)//This takes a problem p and returns a linkedList of problems that would go in the child nodes of the node containing p as outlined in the paper.
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
	
	
	void ftrans(ConstraintChecker c){//This performs a transition using div on the leaf specified by 'selectedIndex'
									//It does this by getting a linked list of problems that would be the problems of the selcted node's children (with the selcted node being specified by 
									//selectedIndex), creating a linked list of Leafs initialized using those problems, then replacing the selected node with the linked list of its children
									//in leaves.
		
		LinkedList<Prob> children = div(leaves.get(selectedIndex).pr);
		
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
	
	 void fleaf(){//This selets a leaf from the list.
	 
		boolean beenSelected = false;//This keeps track of whether an leaf that can be operated on has been found
		int deepest = 0;//This keeps track of the depth of the selected leaf
		int besteval = -1;//This keeps track of the eval of the selected leaf
		
		//Note that the loops below go through the linked list from the right, which is why the indices below look like 'leaves.size()-i-1'
		if(bestsol == -1){//This operates a depth-first leaf selection criterion because no leaf has been found if bestsol is -1
			for (int i = 0; i < leaves.size(); i++) {
				Leaf l = leaves.get(leaves.size()-i-1);
				
				 if(l.sol == '?' && !beenSelected);{//If no node is to be selected and an unsolved node is found, that one is  selected
					selected = l;
					beenSelected = true;
					deepest = l.depth;
					besteval = l.eval;
					selectedIndex = leaves.size()-i-1;
				 }
				 if(l.sol == '?' && l.depth > deepest){//If a node is found that is both unsolved and deeper than the currently selected node, the deeper one is selected
					selected = l;
					deepest = l.depth;
					besteval = Math.min(besteval, l.eval);
					selectedIndex = leaves.size()-i-1;
					
					deepest = l.depth;
				 }
				 else if(l.sol == '?' && l.depth == deepest && l.eval < besteval){//If a node is found that is both unsolved as deep as the selected node, but has a better eval, 
																					//it is selected
					selected = l;
					besteval = l.eval;
					selectedIndex = leaves.size()-i-1;
					
					deepest = l.depth;
				 }
				  else if(l.sol == '?' && l.depth == deepest && l.eval == besteval){//If a node that is identical in all criteria that fleaf considers is found to the left of the current
																					//selected node, it is selected
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
		if(bestsol >= 0){//If a solution has been found, a best-first selecion is used
			 beenSelected = false;
			
			for (int i = 0; i < leaves.size(); i++) {
				Leaf l = leaves.get(leaves.size()-i-1);
			
				//System.out.println(beenSelected);
				
			
				if (l.sol == '?' && !beenSelected) {//If no node is selected and an unsolved node is found, that one is selected
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
				 
				if(l.sol == '?' && l.eval < besteval){//If an unsolved node with a better eval is found, it is selected
					selected = l;
					besteval = l.eval;
					selectedIndex = leaves.size()-i-1;
					deepest = l.depth;
				}
				else if(l.sol == '?' && l.eval == besteval && l.depth > deepest){//If a deeper unsolved node with the same eval as the one that is currently selected is found			
																					//then it is selected
					selected = l;
					deepest = l.depth;
					//besteval = Math.min(besteval, leaves.get(leaves.size()-i-1).eval);
					selectedIndex = leaves.size()-i-1;
				}
				else if(l.sol == '?' && l.eval == besteval && l.depth == deepest){//If a node that is identical in all criteria that fleaf considers is found to the left of the current
																					//selected node, it is selected
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