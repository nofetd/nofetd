package ScalarNumbers;

public class RationalScalar implements Scalar{
	private int cardinal;
	private int denominator;
	
	public RationalScalar (int cardinal, int denominator) {
		if (cardinal<0 && denominator<0) {
		this.cardinal=-cardinal;
		this.denominator=-denominator;
		}
		else
			this.cardinal=cardinal;
		this.denominator=denominator;
	}

	//Returns a scalar which is the sum of the current scalar and the argument
		public Scalar add(Scalar s){
			if (((RationalScalar)s).GetDenominator()==this.GetDenominator()) {
				int card=((RationalScalar)s).GetCardinal()+this.GetCardinal();
				RationalScalar ans= new RationalScalar(card, ((RationalScalar)s).GetDenominator());
				return ans;
			}
			else {
				int denom=((RationalScalar)s).GetDenominator()*this.GetDenominator();
			    int card=((RationalScalar)s).GetCardinal()*this.GetDenominator()+this.GetCardinal()*((RationalScalar)s).GetDenominator();
			    RationalScalar ans= new RationalScalar(card,denom);
			    return ans;
			}
				
		}
		
		//Returns a scalar which is the multiplication of the current scalar and the argument
		public  Scalar mul(Scalar s){
			int card=((RationalScalar)s).GetCardinal()*this.GetCardinal();
			int denom=((RationalScalar)s).GetDenominator()*this.GetDenominator();
			RationalScalar Ans=new RationalScalar(card,denom);
			return Ans;
		}
		
		//Returns a scalar which is the multiplication of the current scalar and the argument
		public  Scalar mul(int  i) {
			int card=this.cardinal*i;
			int denom=this.denominator;
			RationalScalar Ans=new RationalScalar(card,denom);
			return Ans;	
		}
		
		//Returns a scalar which is the power of the current scalar by the exponent
		public Scalar pow(int exponent) {
			int times=exponent;
			if (exponent==0)
				return new  RationalScalar(1,1);
			int card=this.cardinal;
			int denom=this.denominator;
			while (times>1) {
				card=card*card;
			    denom=denom*denom;
				times=times-1;
			}
			RationalScalar Ans=new RationalScalar(card,denom);
			return Ans;
		}
		
		//Returns a scalar which is the result of multiplying the current scalar by (-1)
	    public Scalar neg(){
	    	int card=this.GetCardinal()*-1;
	    	int denom=this.GetDenominator();
	    	RationalScalar Ans=new RationalScalar(card,denom);
			return Ans;
	    }
	    
	    //Returns true if the argument Scalar and the current Scalar have the numeric same value
	    public boolean equals(Scalar s) {
	    return (this.GetCardinal()*((RationalScalar)s).GetDenominator()==this.GetDenominator()*((RationalScalar)s).GetCardinal());
	    }
	    
	    public int GetCardinal(){
	    	return this.cardinal;
	    }
	    public int GetDenominator() {
	    	return this.denominator;
	    }
	    
	    public void setcardinal(int cardinal) {
	    	this.cardinal=cardinal;
	    }
	    
	    public void setdenominator(int denominator) {
	    	this.denominator=denominator;
	    }
	    
	    //return the value by a string
	    public String toString() {
	    	if (this.cardinal==this.denominator) {
	    		return "1";
	    	}
	    	String card=Integer.toString(this.cardinal);
	    	String denom=Integer.toString(this.denominator);
	    	String ans=card+"/"+denom;
	    	return ans;
}
	    
	    public String geTipe() {
	    	return "Q";
	    }
}
