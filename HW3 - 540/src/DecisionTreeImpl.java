import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	
  private DecTreeNode root;
  
  //ordered list of class labels
  private List<String> labels; 
  
  //ordered list of attributes
  private List<String> attributes; 
  
  //map to ordered discrete values taken by attributes
  private Map<String, List<String>> attributeValues; 
  
  /**
   * Answers static questions about decision trees.
   */
  DecisionTreeImpl() {
    // no code necessary this is void purposefully
  }

  /**
   * Build a decision tree given only a training set.
   * 
   * @param train: the training set
   */
  DecisionTreeImpl(DataSet train) {

    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    // TODO: add code here
    
    String majorityLabel = MajorityLabel(train);
    this.root = BuildTree(train, train.attributes, majorityLabel);
   
  }
  
  String MajorityLabel(DataSet tree){
	  
	  Map<String, Integer> map = new HashMap<String, Integer>();
	  for (String label : tree.labels){
		  Integer freq = map.get(label);
		  map.put(label, (freq == null) ? 1 : freq + 1);
	  }
	  
	  int max = -1;
	  String mostFrequent = null;
	  for(Map.Entry<String, Integer> labelEntry: map.entrySet()){
		  if(labelEntry.getValue() > max){
			  mostFrequent = labelEntry.getKey();
			  max = labelEntry.getValue();
		  }
	  }
	  return mostFrequent;
  }
  
  int InformationGain(List<String> attributes, List<Instance> instances){
	  // Calculate H(class)
	  
	return 1;  
  }
  
  DecTreeNode BuildTree(DataSet childTree, List<String> attributes, String defaultLabel){
	  
	  //test for all same classification
	    String x = "begin";
	    boolean sameLabel = true;
	    for(String check: labels){
	    	if(!x.contentEquals("begin")){
	    		if(check != x){
	    			sameLabel = false;
	    		}
	    	}
	    	x = check;
	    }
	    
	    if(childTree.instances.isEmpty()){
	    	// return terminal node with plurality value of parent example labels
	    	return new DecTreeNode(defaultLabel, null, null, true);
	    }else if(sameLabel){
	    	// return terminal node with label shared by all examples in tree
	    	return new DecTreeNode(labels.get(0), null, null, true);
	    }else if(attributes.isEmpty()){
	    	// return terminal node with plurality value of current example labels
	    	return new DecTreeNode(MajorityLabel(childTree), null, null, true);
	    }else{
	    	// A = argmax for attribute a of IMPORTANCE(a, examples)
	    	int importantAttribute = 
	    	// calculate majorityLabel
	    	
	    	// tree = new decision tree with root node A
	    	DecTreeNode treeToReturn = 
	    	// create subtrees for each attribute value
	    	
	    	//
	    	return treeToReturn;
	    }
	    
  }

  /**
   * Build a decision tree given a training set then prune it using a tuning set.
   * 
   * @param train: the training set
   * @param tune: the tuning set
   */
  DecisionTreeImpl(DataSet train, DataSet tune) {

    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    // TODO: add code here
    
  }

  @Override
  public String classify(Instance instance) {

    // TODO: add code here
	  
	return "TODO";
  }

  @Override
  public void rootInfoGain(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    // TODO: add code here
    
  }
  
  @Override
  /**
   * Print the decision tree in the specified format
   */
  public void print() {

    printTreeNode(root, null, 0);
  }

  /**
   * Prints the subtree of the node with each line prefixed by 4 * k spaces.
   */
  public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append("    ");
    }
    String value;
    if (parent == null) {
      value = "ROOT";
    } else {
      int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
      value = attributeValues.get(parent.attribute).get(attributeValueIndex);
    }
    sb.append(value);
    if (p.terminal) {
      sb.append(" (" + p.label + ")");
      System.out.println(sb.toString());
    } else {
      sb.append(" {" + p.attribute + "?}");
      System.out.println(sb.toString());
      for (DecTreeNode child : p.children) {
        printTreeNode(child, p, k + 1);
      }
    }
  }

  /**
   * Helper function to get the index of the label in labels list
   */
  private int getLabelIndex(String label) {
    for (int i = 0; i < this.labels.size(); i++) {
      if (label.equals(this.labels.get(i))) {
        return i;
      }
    }
    return -1;
  }
 
  /**
   * Helper function to get the index of the attribute in attributes list
   */
  private int getAttributeIndex(String attr) {
    for (int i = 0; i < this.attributes.size(); i++) {
      if (attr.equals(this.attributes.get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
   */
  private int getAttributeValueIndex(String attr, String value) {
    for (int i = 0; i < attributeValues.get(attr).size(); i++) {
      if (value.equals(attributeValues.get(attr).get(i))) {
        return i;
      }
    }
    return -1;
  }
}
