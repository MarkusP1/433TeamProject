public class Leaf{
	Prob pr;
	char sol; // can be y or ?
	int depth;
	int eval;//despite the name tracks eval*
	boolean constr;

	
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
		eval = pr.getEval();
	}
	void setConstr(ConstraintChecker c){
		c.constrStar(pr);
		constr = pr.getConstr();
	}
}