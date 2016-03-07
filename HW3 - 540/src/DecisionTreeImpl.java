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

	float Entropy(List<Float> proportions){
		float entropy = 0;
		for(float proportion : proportions){
			entropy += (-proportion)*Math.log(proportion)/Math.log(2);
		}

		return entropy;
	}
	Map<String, Float> InformationGain(List<Instance> examples){


		// get frequency of all labels (R, L, B)

		Map<String, Integer> labelFreqMap = new HashMap<String, Integer>();
		for(Instance example: examples){
			Integer freq = labelFreqMap.get(example.label);
			labelFreqMap.put(example.label, (freq == null) ? 1 : freq + 1);
		}

		// Calculate H(label): H(Y)
		List<Float> proportions = new ArrayList<Float>();
		for(Map.Entry<String, Integer> labelEntry: labelFreqMap.entrySet()){
			float propOfYi = (float)labelEntry.getValue()/examples.size();
			proportions.add(propOfYi);	

		}

		float HLabel = Entropy(proportions);



		// get frequency of each value for each attribute (1 A0 = 0, 3 A0 = 1, etc) 


		Map<AttributeValuePair, Integer> AttributeValueFreqMap = new HashMap<AttributeValuePair, Integer>();
		List<HashMap<AttributeValuePair, Integer>>  LabelAttributeValueMap = new ArrayList<HashMap<AttributeValuePair, Integer>>();

		for(int i = 0; i < labels.size(); i++){
			HashMap<AttributeValuePair, Integer> newMap = new HashMap<AttributeValuePair, Integer>();
			LabelAttributeValueMap.add(i, newMap);
		}



		for(Instance example : examples){
			for(int i = 1; i <= example.attributes.size(); i++){

				AttributeValuePair newPair = new AttributeValuePair(i, Integer.parseInt(example.attributes.get(i-1)));

				for(int k = 0; k < labels.size(); k++){
					if(labels.get(k).equals(example.label)){

						// Get freq of labels given attributes: Pr(Y = yi | X = v)
						Integer freq = LabelAttributeValueMap.get(k).get(newPair);
						LabelAttributeValueMap.get(k).put(newPair, (freq == null) ? 1 : freq + 1);

					}
				}

				// Freq of v given X
				Integer freq = AttributeValueFreqMap.get(newPair);
				AttributeValueFreqMap.put(newPair, (freq == null) ? 1 : freq + 1);
			}
		}

		/*
		//TEST CODE//////////////////////
		for(int k = 0; k < labels.size(); k++){
			for (Map.Entry<AttributeValuePair, Integer> name: LabelAttributeValueMap.get(k).entrySet()){

				String attribute = Integer.toString(name.getKey().getAttribute());
				String pairValue = Integer.toString(name.getKey().getValue());
				String value = name.getValue().toString();  
				System.out.println(k + " " + attribute + " " + pairValue + " " + value);  


			} 
		}
		//END TEST CODE////////////////
		 */

		// Calculate H(Label | AttributeValue): H(Y | X = v) = Sum of -Pr(Y = yi | X = v)log2(Pr(Y = yi | X = v))



		Map<Integer, Float> AttributeEntropy = new HashMap<Integer, Float>();
		for(String attribute: attributes){
			System.out.println(attribute);
		}


		for(Map.Entry<AttributeValuePair, Integer> labelEntry: AttributeValueFreqMap.entrySet()){
			if(attributes.contains("A" + labelEntry.getKey().getAttribute())){
				//System.out.println(labelEntry.getKey().getAttribute());
				AttributeValuePair newPair = labelEntry.getKey();
				float HLAVSum = 0;
				List<Float> proportionsHYXv = new ArrayList<Float>();
				//new AttributeValuePair(labelEntry.getKey().getAttribute(), labelEntry.getKey().getValue());
				for(int i = 0; i < LabelAttributeValueMap.size(); i++){
					if(LabelAttributeValueMap.get(i).containsKey(newPair)){
						//System.out.println("value" + LabelAttributeValueMap.get(i).get(newPair));
						//System.out.println("entry" + labelEntry.getValue());
						float propOfYiXv = (float) 
								(LabelAttributeValueMap.get(i).get(newPair)/(float)labelEntry.getValue());
						//System.out.println("prop" + propOfYiXv);
						proportionsHYXv.add(propOfYiXv);	
					}
				}

				if(AttributeEntropy.get(newPair.getAttribute()) == null){
					AttributeEntropy.put(newPair.getAttribute(), (float) 0);
					System.out.println(newPair.getAttribute());
				}
				//System.out.println("yo " + proportionsHYXv.size());
				HLAVSum = Entropy(proportionsHYXv);
				//System.out.println("HLAV " + HLAVSum);
				float Xv = (float) labelEntry.getValue()/ (float) examples.size();
				float HYXpart = HLAVSum*Xv;
				//System.out.println("HYX " + HYXpart);
				float HYXSum = AttributeEntropy.get(
						Integer.valueOf(newPair.getAttribute()));

				//System.out.println("attr" + Integer.valueOf(newPair.getAttribute()));

				AttributeEntropy.put(Integer.valueOf(newPair.getAttribute()),
						(HYXSum == 0) ? (HYXpart) : (HYXSum + HYXpart));
			}

		}


		Map<String, Float> IGMap = new HashMap<String, Float>();
		//System.out.println("attributesize: " + AttributeEntropy.size());
		for(Map.Entry<Integer, Float> entry : AttributeEntropy.entrySet()){
			IGMap.put(Integer.toString(entry.getKey()), HLabel - entry.getValue());
			//	System.out.println(entry.getValue());
		}
		/*
		for(int i = 0; i < AttributeEntropy.size(); i++){
			IGMap.put(Integer.toString(i + 1), HLabel - AttributeEntropy.get(i));
		}
		 */



		return IGMap;  


	}

	DecTreeNode BuildTree(List<Instance> examples, List<String> attributes, String defaultLabel){

		this.attributes = attributes;

		//TEST
		System.out.print("parent: ");
		for(String attribute: attributes){
			System.out.print(attribute);
		}
		System.out.println("");
		//TEST

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
			Map<String, Float> IGMap = InformationGain(examples);

			double max = -1;
			String importantAttribute = null;
			for(Map.Entry<String, Float> IGEntry: IGMap.entrySet()){
				if(IGEntry.getValue() > max){
					importantAttribute = IGEntry.getKey();
					max = IGEntry.getValue();
				}
			}

			System.out.println("Attribute: " + importantAttribute);
			// calculate majorityLabel
			String majorityLabel = MajorityLabel(examples);

			// tree = new decision tree with root node A
			DecTreeNode treeToReturn = new DecTreeNode(majorityLabel, "A" + importantAttribute, null, false);

			// create subtrees for each attribute value

			for(String value : attributeValues.get("A" + importantAttribute)){

				// Get subset of examples with importantAttribute == value
				List<Instance> exs = new ArrayList<Instance>();

				for(Instance example: examples){

					if(example.attributes.get(getAttributeIndex(importantAttribute)).equals(value)){
						exs.add(example);

					}

				}

				// Pass attributes minus the one being used to create children
				List<String> childAttributes = attributes;
				for(String attribute: childAttributes){
					System.out.println(attribute);
				}
				for(int i = 0; i < childAttributes.size(); i++){
					if(childAttributes.get(i).toString().equals("A" + importantAttribute)){
						childAttributes.remove(i);
					}
				}

				//TEST
				System.out.print("children: ");
				for(String attribute: childAttributes){
					System.out.print(attribute);
				}
				System.out.println("");
				//TEST



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

		Map<String, Float> IGMap = InformationGain(train.instances);


		for(Map.Entry<String, Float> IGEntry: IGMap.entrySet()){
			System.out.format("%s %.5f\n", IGEntry.getKey(), IGEntry.getValue());
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
