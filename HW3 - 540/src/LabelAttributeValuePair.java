
public class LabelAttributeValuePair {

	private String label;
	private String attribute;
	LabelAttributeValuePair(String label, String attribute){
		this.label = label;
		this.attribute = attribute;
	}
	
	String getLabel(){
		return this.label;
	}
	
	String getAttribute(){
		return this.attribute;
	}
	
	@Override
	public int hashCode(){
		int hash = 1;
		hash = hash * 17 + label.hashCode();
		hash = hash * 31 + attribute.hashCode();
		return hash;
	}
	
}
