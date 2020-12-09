public class Leaf{
	Prob pr;//This is the partial or whole assignment the leaf stores.
	char sol; // can be y or ?
	int depth;
	int eval;//despite the name tracks eval*
	boolean constr;
	int fbound; 

	
	Leaf(Prob pr, int depth){
		this.pr = pr;
		/*this.eval = pr.eval;
		this.fbound = pr.fbound;
		this.constr = pr.constr;*/
		this.depth = depth;
		this.sol = '?';
	}
	
	void setSol(char s){
		this.sol = s;
	}
	
	void setEval(ConstraintChecker c){//This sets an eval based on the ConstraintChecker passed to it
		c.evalStar(pr);
		c.fbound(pr);
		eval = pr.getEval();
		fbound = pr.getFbound();
	}
	void setConstr(ConstraintChecker c){//This sets a constr based on the ConstraintChecker passed to it
		c.constrStar(pr);
		constr = pr.getConstr();
	}
	
	public String toString() {
		return "Leaf: " + pr.toString() + "\n"
				+ sol + " " + depth + " " + eval + " " + constr;
	}
}