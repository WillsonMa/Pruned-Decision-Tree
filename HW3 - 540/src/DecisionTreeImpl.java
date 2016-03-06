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

		String majorityLabel = MajorityLabel(train.instances);
		this.root = BuildTree(train.instances, train.attributes, majorityLabel);

	}

	String MajorityLabel(List<Instance> examples){

		Map<String, Integer> labelMap = new HashMap<String, Integer>();

		for(Instance example : examples){

			Integer freq = labelMap.get(example.label);
			labelMap.put(example.label, (freq == null) ? 1 : freq + 1);

		}

		int max = -1;
		String mostFrequent = null;
		for(Map.Entry<String, Integer> labelEntry: labelMap.entrySet()){
			if(labelEntry.getValue() > max){
				mostFrequent = labelEntry.getKey();
				max = labelEntry.getValue();
			}
		}
		return mostFrequent;
	}

	Map<String, Double> InformationGain(List<Instance> examples){


		// get frequency of all attribute values (1 R, 2 G, 4 B) etc

		Map<String, Integer> map = new HashMap<String, Integer>();
		for(String attribute : attributes){
			for(String value : attributeValues.get(attribute)){
				Integer freq = map.get(value);
				map.put(value, (freq == null) ? 1 : freq + 1);
			}
		}

		// Calculate H(attribute)
		Map<String, Double> HClassMap = new HashMap<String, Double>();

		for(String attribute : attributes){
			for(String value : attributeValues.get(attribute)){
				double currHClass = (-map.get(value)/attributeValues.size())*Math.log(map.get(value)/attributeValues.size())/Math.log(2);
				double HClassSum = HClassMap.get(attribute);
				HClassMap.put(attribute, (HClassSum + currHClass));
			}

		}

		// Calculate H(Attribute | Other Attribute's Value): H(Y|X=v)

		// Calculate H(Attribute | Other Attribute): H(Y | X)

		// Calculate Information Gain(Y ; X)

		return "TODOSTRING";  
	}

	DecTreeNode BuildTree(List<Instance> examples, List<String> attributes, String defaultLabel){

		this.attributes = attributes;
		//test for all same classification
		String unanimousLabel = "begin";
		boolean sameLabel = true;
		for(Instance example: examples){
			if(!unanimousLabel.contentEquals("begin")){
				if(!example.label.contentEquals(unanimousLabel)){
					sameLabel = false;
				}
			}
			unanimousLabel = example.label;
		}

		if(examples.isEmpty()){
			// return terminal node with plurality value of parent example labels
			return new DecTreeNode(defaultLabel, null, null, true);

		}else if(sameLabel){
			// return terminal node with label shared by all examples in tree
			return new DecTreeNode(unanimousLabel, null, null, true);

		}else if(attributes.isEmpty()){
			// return terminal node with plurality value of current example labels
			return new DecTreeNode(MajorityLabel(examples), null, null, true);

		}else{
			// importantAttribute = argmax for attribute a of IMPORTANCE(a, examples),
			Map<String, Double> IGMap = InformationGain(examples);

			double max = -1;
			String importantAttribute = null;
			for(Map.Entry<String, Double> IGEntry: IGMap.entrySet()){
				if(IGEntry.getValue() > max){
					importantAttribute = IGEntry.getKey();
					max = IGEntry.getValue();
				}
			}

			// calculate majorityLabel
			String majorityLabel = MajorityLabel(examples);

			// tree = new decision tree with root node A
			DecTreeNode treeToReturn = new DecTreeNode(majorityLabel, importantAttribute, null, false);

			// create subtrees for each attribute value
			for(String value : attributeValues.get(importantAttribute)){

				// Get subset of examples with importantAttribute == value
				List<Instance> exs = new ArrayList<Instance>();
				for(Instance example: examples){
					for(String exampleValue : example.attributes){
						if(exampleValue.equals(value)){
							exs.add(example);
							break;
						}
					}
				}

				// Pass attributes minus the one being used to create children
				List<String> childAttributes = attributes;
				childAttributes.remove(importantAttribute);

				// Build subtree
				DecTreeNode childTree = BuildTree(exs, childAttributes, majorityLabel);

				// Add arc from tree to subtree
				treeToReturn.addChild(childTree);
			}
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
		DecTreeNode traversalNode = root;
		while(!traversalNode.terminal){
			for(DecTreeNode child : traversalNode.children){
				if(child.terminal){
					return child.label;
				}else if(instance.attributes.contains(child.attribute)){
					traversalNode = child;
					break;
				}

			}
		}
		return traversalNode.label;
	}

	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		// TODO: add code here

		Map<String, Double> IGMap = InformationGain(train.instances);

		for(Map.Entry<String, Double> IGEntry: IGMap.entrySet()){
			System.out.print(IGEntry.getKey() + " ");
			System.out.format("%.5f\n", IGEntry.getValue());
			IGEntry.getValue();
		}


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
