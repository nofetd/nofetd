package PolynomialExpressions;
import ScalarNumbers.*;

public class PolyTerm  {

	private Scalar coefficient;
	private int exponent;

	public PolyTerm(Scalar confficient, int exponent) {
		this.coefficient=confficient;
		this.exponent=exponent;
	}

	//returns true if the argument PolyTerm can be added
	public boolean canAdd(PolyTerm pt) {
		return pt.exponent==this.exponent;
	}

	//returns new PolyTerm which is the result of adding the current PolyTerm and the argument.
	public PolyTerm add(PolyTerm pt) {
		Scalar coeffi=this.coefficient.add(pt.coefficient);
		int expon=pt.exponent;
		return new PolyTerm(coeffi, expon);
	}

	//return new PolyTerm which is the result of multiplying the current PolyTerm and the argument.
	public PolyTerm mul(PolyTerm pt) {
		Scalar coeffi=this.coefficient.mul(pt.coefficient);
		int expon= pt.exponent + this.exponent;
		return new PolyTerm(coeffi, expon);
	}

	//evaluates the current term using the scalar
	public Scalar evaluate(Scalar scalar) {
		Scalar ans=scalar;
		ans=ans.pow(this.exponent);
		return this.coefficient.mul(ans);
	}

	//returns PolyTerm which is the result of the derivation on the current PolyTerm 
	public PolyTerm derivate() {
		Scalar coeffi=this.coefficient.mul(this.exponent);
		int expon=this.exponent-1;
		return new PolyTerm(coeffi, expon);
	}

	//returns true if the argument PolyTerm is equal to the current PolyTerm 
	public boolean equals(PolyTerm pt) {
		return this.exponent==pt.exponent&&this.coefficient.equals(pt);
	}

	public String toString() {
		if (this.exponent==0) {
			return this.coefficient.toString();
		}
		else
			if (this.exponent==1) {
				if(this.coefficient.toString()=="1"){
					return "x";
				}
				else if(this.coefficient.toString()=="-1")
					return "-x";
				else
					return this.coefficient.toString()+"x";
			}
		String scalar=this.coefficient.toString();
		String expon=Integer.toString(exponent);
		if (scalar=="1") {
			return "x^"+expon;
		}
		else if(scalar=="-1")
			return "-x^"+expon;
		String ans= scalar+"x^"+expon;
		return ans;
	}

	public Scalar getcoefficient() {
		return this.coefficient;
	}

	public int getexponent() {
		return this.exponent;
	} 
	public String geTipe() {
    	return this.coefficient.geTipe();
    }
}



