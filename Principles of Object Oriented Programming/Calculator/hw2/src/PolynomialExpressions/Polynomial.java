package PolynomialExpressions;
import ScalarNumbers.*;

import java.util.LinkedList;
import java.util.Comparator;

public class Polynomial implements Comparator<PolyTerm>{
	
	private LinkedList<PolyTerm> polynom;
	
	//this constructor gets a sort LinkedList by the exponent of the polyterms in the array
	
	public Polynomial(LinkedList<PolyTerm> Polynom) {
		this.polynom=new LinkedList<PolyTerm>();
		for (int i=0; i<Polynom.size();i++) {
			polynom.add(Polynom.get(i));
		}
	}

	
	//returns a Polynomial which is the sum of the current Polynomial with the argument
	public Polynomial add(Polynomial poly) {
		LinkedList<PolyTerm> p= poly.polynom;
		LinkedList<PolyTerm> output=new LinkedList<PolyTerm>();
		int i=0;
		while(i<this.polynom.size()) {
			PolyTerm x= this.polynom.get(i);
			boolean flag=false;                               //for stopping the loop if the exponent is found 
			int j=0;                                          //index for the list of the argument
			while(j<p.size() & !flag) {
				if(x.canAdd(p.get(j)))
				{           
					output.add(x.add(p.get(j)));
					flag=p.remove(p.get(j));                   //the suitable exponent is found
				}
				else j++;
			}
			if(j==p.size()&&!flag)
				output.add(x);
			i++;
		}
		int k=0;
		while(k<p.size())
		{
			output.add(p.get(k));
			k++;
		}
		return new Polynomial(output);
	}
	
	
	//returns a Polynomial is the multiplication of the current Polynomial with the argument
	public Polynomial mul(Polynomial poly) {
		LinkedList<PolyTerm> output=new LinkedList<PolyTerm>();
		for(int i=0;i<this.polynom.size();i++)
		{
			for(int j=0; j<poly.polynom.size();j++)
			output.add(this.polynom.get(i).mul(poly.polynom.get(j)));
		}
		return new Polynomial(output);
	}
	
	
	//evaluates the polynomial using the argument scalar
	public Scalar evaluate(Scalar scalar) {
		Scalar s;
		if(this.polynom.get(0).geTipe().equals("Q"))
		s=new RationalScalar (0, 1);
		else
			s=new RealScalar(0);
		for(int i=0; i<this.polynom.size(); i++)
		{
			s= s.add(this.polynom.get(i).evaluate(scalar));
		}
		return s;
	}
	
	//return the Polynomial which is the result of applying first order derivation on the current Polynomial
	public Polynomial derivate() {
		LinkedList<PolyTerm> output=new LinkedList<PolyTerm>();
		for(int i=0; i<this.polynom.size();i++)
			output.add(this.polynom.get(i).derivate());
		return new Polynomial(output);
	}
	
	
	public String toString() {
		if(this.polynom==null)
			throw new IllegalArgumentException();
		if(this.polynom.isEmpty())
			return "";
		Polynomial copy= new Polynomial(this.polynom);
		PolyTerm max=this.findMax(copy.polynom);
		String output=max.toString();
		copy.polynom.remove(max);
		for(int i=1; i<this.polynom.size();i++)
		{
			max= this.findMax(copy.polynom);
			if(max.toString().charAt(0)=='-')
				output= output+max.toString();
			else output=output+'+'+max.toString();
			copy.polynom.remove(max);	
		}
		return output;
		
	}
	
	
	//returns true if the argument polynomial is equal to the current polynomial
	public boolean equals(Polynomial poly) {
		String currentPoly=this.toString();
		String argumentPoly=poly.toString();
		return currentPoly.equals(argumentPoly);
	}
	
	
	//A method which removes the max PolyTerm from the List and returns it
	private PolyTerm findMax(LinkedList<PolyTerm> polynom) {
		if(polynom==null)
			throw new IllegalArgumentException();
		PolyTerm max=polynom.get(0);
		for(int i=1; i<polynom.size(); i++)
		{
			if(compare(polynom.get(i),max)>0)
				max=polynom.get(i);
		}
		return max;
	}
	
	
	public int compare(PolyTerm o1, PolyTerm o2) {
		return o1.getexponent()-o2.getexponent();
	}
	
	
	
	
	
	
}
