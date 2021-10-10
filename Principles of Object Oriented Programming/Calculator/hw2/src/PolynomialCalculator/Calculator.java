package PolynomialCalculator;
import java.util.Scanner;
import PolynomialExpressions.*;
import ScalarNumbers.*;
import java.util.LinkedList;

public class Calculator {
	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);

		String tiPolynom;
		int scalarFile;
		String firstPolynomial;
		String SecondPolynomial = null;
		String ValueByScalar = null;
		Polynomial ans = null;
		Scalar ansByValue = null;

		/*tiPolynom="R";
		scalarFile=3;
		firstPolynomial="5x^4+7x";
		SecondPolynomial =null;
		ValueByScalar ="2.7";
		ans = null;
		ansByValue = null;
		*/
		
		System.out.println("Please select an operation:" + "\n"+
		"                         " + "1. Addition" + "\n" + 
		"                         " + "2. Multiplication" + "\n" + 
		"                         " + "3. Evaluation" + "\n" + 
		"                         " + "4. Derivate"  + "\n" + 
		"                         " + "5. Exit");
	    scalarFile = input.nextInt();
	    
	    while(scalarFile!=5) {
	    	System.out.println("Please select the scalar field\r\n" + "Rational (Q) or Real (R)");
		    tiPolynom = input.next();

		    System.out.println("Please insert the first polynomial");
		    firstPolynomial = input.next();

		    if (scalarFile==1|scalarFile==2) {
		    	System.out.println("Please insert the second polynomial");
			    SecondPolynomial = input.next();
			    }
		    if (scalarFile==3) {
		    	System.out.println("Please insert the scalar");
			    ValueByScalar = input.next();
			    if ((tiPolynom.equals("Q")|tiPolynom.equals("q"))&&ValueByScalar.contains(".")) {
			    	System.out.println("the output is not valiable, please press number without '.''");
				    ValueByScalar = input.next();
				    }
			    }
		    if (tiPolynom.equals("Q")|tiPolynom.equals("q")){
		    	Polynomial first =changToQ(firstPolynomial);
		    	if (scalarFile==1) {
		    		Polynomial second =changToQ(SecondPolynomial);
		    		ans = first.add(second);
		    		}
		    	if (scalarFile==2) {
		    		Polynomial second =changToQ(SecondPolynomial);
		    		ans = first.mul(second);
		    		}
		    	if (scalarFile==3) 
		    		ansByValue = first.evaluate(changToQScalar(ValueByScalar));
		    	if (scalarFile==4)
		    		ans=first.derivate();	
		    	}

		    if (tiPolynom.equals("R")|tiPolynom.equals("r")){
		    	Polynomial first =changToR(firstPolynomial);
		    	if (scalarFile==1) {
		    		Polynomial second =changToR(SecondPolynomial);
		    		ans = first.add(second);
		    		}
		    	if (scalarFile==2) {
		    		Polynomial second =changToR(SecondPolynomial);
		    		ans = first.mul(second);
		    		}
		    	if (scalarFile==3)
		    		ansByValue = first.evaluate(changToRScalar(ValueByScalar));
		    	if (scalarFile==4)
		    		ans=first.derivate();	
		    	}
		    if (scalarFile==1|scalarFile==2|scalarFile==4)
		    	System.out.println(ans.toString());
		    if (scalarFile==3)
		    	System.out.println(ansByValue.toString());
		    System.out.println("Please select an operation:" + "\n"+
	    		"                         " + "1. Addition" + "\n" + 
	    		"                         " + "2. Multiplication" + "\n" + 
	    		"                         " + "3. Evaluation" + "\n" + 
	    		"                         " + "4. Derivate"  + "\n" + 
	    		"                         " + "5. Exit");
		    scalarFile = input.nextInt();
		    }
	    input.close();
	    }


	
	
	
	
	
	
	
	
	private static Polynomial changToQ(String toChange){
		String[] parts=toChange.split("(?=[+-])");
		String[] fixparts= new String[parts.length];
		for (int i=0; i<parts.length;i++) {              //fix the problem when the polynomial from that shape 2/-3
			if (parts[i].charAt(parts[i].length()-1)=='/'&& parts[i+1].charAt(0)=='-') {
				fixparts[i]=parts[i]+parts[i+1];
				i=i+1;
			}
			else
				fixparts[i]=parts[i];
		}     
		LinkedList<PolyTerm> ans=new LinkedList<PolyTerm>();

		for (int i=0; i<fixparts.length&&fixparts[i]!=null;i++) {
			int exponent=0;
			int cardinal=1;
			int denominator=1;
			String[] part=fixparts[i].split("(?=[/x])");
			for (int j=0; j<part.length;j++) {
				if (part[j].contains("/"))
					denominator=Integer.parseInt(part[j].substring(1));
				else
					if (part[j].contains("x")&part[j].contains("^"))
						exponent=Integer.parseInt(part[j].substring(2));
					else
						if (part[j].equals("x"))
							exponent=1;
						else
							cardinal=Integer.parseInt(part[j]);
			}
			RationalScalar Scalar= new RationalScalar (cardinal,denominator);
			ans.add(new PolyTerm (Scalar,exponent));
		}
		return new Polynomial(ans);
	}

	private static Polynomial changToR(String toChange){
		String[] parts=toChange.split("(?=[+-])");
		String[] fixparts= new String[parts.length];
		for (int i=0; i<parts.length;i++) {              //fix the problem when the polynomial from that shape 2/-3
			if (parts[i].charAt(parts[i].length()-1)=='/'&& parts[i+1].charAt(0)=='-') {
				fixparts[i]=parts[i]+parts[i+1];
				i=i+1;
			}
			else
				fixparts[i]=parts[i];
		}

		LinkedList<PolyTerm> ans=new LinkedList<PolyTerm>();

		for (int i=0; i<fixparts.length&&fixparts[i]!=null;i++) {
			int exponent=0;
			double value=1;
			String[] part=fixparts[i].split("(?=[x])");
			for (int j=0; j<part.length;j++) {
				if (!part[j].contains("x"))
					value=Double.parseDouble(part[j]);
				else
					if (part[j].contains("x")&part[j].contains("^"))
						exponent=Integer.parseInt(part[j].substring(2));
					else
						if (part[j].equals("x"))
							exponent=1;
			}
			RealScalar Scalar= new RealScalar (value);
			ans.add(new PolyTerm (Scalar,exponent));
		}
		return new Polynomial(ans);
	}

	private static Scalar changToRScalar(String toChange){
		return new RealScalar(Double.parseDouble(toChange));
	}

	private static Scalar changToQScalar(String toChange){
		RationalScalar a;
		int index=toChange.indexOf("/");
		if(index>0) {
			int cardinal=Integer.parseInt(toChange.substring(0,index));
			int denominator=Integer.parseInt(toChange.substring(index+1));	
			a=new RationalScalar(cardinal, denominator);
		}
		else {
			a=new RationalScalar(Integer.parseInt(toChange), 1);
		}
		return a;
	}
}



