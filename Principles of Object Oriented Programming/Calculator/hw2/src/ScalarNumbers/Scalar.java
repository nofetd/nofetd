package ScalarNumbers;


public interface Scalar {
	
	//Returns a scalar which is the sum of the current scalar and the argument
	public Scalar add(Scalar s);
	
	//Returns a scalar which is the multiplication of the current scalar and the argument
	public  Scalar mul(Scalar  s);
	
	//Returns a scalar which is the multiplication of the current scalar and the argument
		public  Scalar mul(int  i);
		
	//Returns a scalar which is the power of the current scalar by the exponent
	public Scalar pow(int exponent);
	
	//Returns a scalar which is the result of multiplying the current scalar by (-1)
    public Scalar neg();
    
    //Returns true if the argument Scalar and the current Scalar have the numeric same value
    public boolean equals(Scalar s);
    
    //return the value by a string
    public String toString();
    
    //return the tipe of the scalar
    public String geTipe() ;
    
    
}
