package ScalarNumbers;

public class RealScalar implements Scalar {
private double value;
	
	public RealScalar(double value) {
		this.value=value;
	}

	@Override
	public Scalar add(Scalar s) {
		
		double output= ((RealScalar)s).value + this.value;
		return new RealScalar(output);
		
	}

	@Override
	public Scalar mul(Scalar s) {
		double output= ((RealScalar)s).value * this.value;
		return new RealScalar(output);
	}
	
	//Returns a scalar which is the multiplication of the current scalar and the argument
	public  Scalar mul(int  i) {
		double value=this.value*i;
		RealScalar Ans=new RealScalar(value);
		return Ans;	
	}

	@Override
	public Scalar pow(int exponent) {
		double output=1;
		for(int i=1;i<=exponent;i++)
		{
			output=output*this.value;
		}
		return new RealScalar(output);
	}

	@Override
	public Scalar neg() {
		return new RealScalar((-1)*this.value);
	}

	@Override
	public boolean equals(Scalar s) {
		return this.value == ((RealScalar)s).value;
	}
	
	//return the value by a string
    public String toString() {
    	
    	String value=Double.toString(this.value);
    	String[] splitvalue=value.split("(?=[.])");
    	if (splitvalue.length>1)
    	value= splitvalue[0]+cut(splitvalue[1]);
    	return value;
    }
	
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double newValue) {
		this.value=newValue;
	}
	
	 public String geTipe() {
	    	return "R";
	    }
	private String cut(String stringToCut) {
		String ans=stringToCut;
		if (ans.length()>4)
			ans=ans.substring(0, 4);
		return ans;
			
	}
}
