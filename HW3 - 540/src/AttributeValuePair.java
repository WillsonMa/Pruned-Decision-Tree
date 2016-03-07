public class AttributeValuePair {

	private int value;
	private int attribute;
	AttributeValuePair(int attribute, int value){
		this.value = value;
		this.attribute = attribute;
	}
	
	int getValue(){
		return this.value;
	}
	
	int getAttribute(){
		return this.attribute;
	}
	
	@Override
	public int hashCode(){
		int hash = 1;
		hash = hash * 17 + value;
		hash = hash * 31 + 3*attribute;
		return hash;
	}
	
	  // Overriding equals() to compare two Complex objects
    @Override
    public boolean equals(Object o) {
 
        // If the object is compared with itself then return true  
        if (o == this) {
            return true;
        }
 
        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof AttributeValuePair)) {
            return false;
        }
         
        // typecast o to Complex so that we can compare data members 
        AttributeValuePair c = (AttributeValuePair) o;
         
        // Compare the data members and return accordingly 
        return Double.compare(value, c.value) == 0
                && Double.compare(attribute, c.attribute) == 0;
    }
}
