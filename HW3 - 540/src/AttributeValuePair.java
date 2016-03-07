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
		hash = hash * 31 + attribute;
		return hash;
	}
	
}
