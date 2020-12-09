public class Leaf{
	Prob pr;
	char sol; // can be y or ?
	int depth;
	int eval;//despite the name tracks eval*
	boolean constr;
	int fbound; 

	
	Leaf(Prob pr, int depth){
		this.pr = pr;
		this.depth = depth;
		this.sol = '?';
	}
	
	void setSol(char s){
		this.sol = s;
	}
	
	void setEval(ConstraintChecker c){
		c.evalStar(pr);
		c.fbound(pr);
		eval = pr.getFbound();
	}
	void setConstr(ConstraintChecker c){
		c.constrStar(pr);
		constr = pr.getConstr();
	}
	
	public String toString() {
		return "Leaf: " + pr.toString() + "\n"
				+ sol + " " + depth + " " + eval + " " + constr;
	}
}