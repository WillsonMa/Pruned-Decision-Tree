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

		String majorityLabel = MajorityLabel(train.instances);
		this.root = BuildTree(train.instances, train.attributes, majorityLabel, null);

	}

	/*
	 * Calculates and returns most frequent label in a list of examples
	 */
	String MajorityLabel(List<Instance> examples){

		Map<String, Integer> labelMap = new HashMap<String, Integer>();

		// create map of labels to frequency
		for(Instance example : examples){

			Integer freq = labelMap.get(example.label);
			labelMap.put(example.label, (freq == null) ? 1 : freq + 1);

		}

		// find label with most frequency
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

	/*
	 * Calculates and returns H(p) = Sum for i = 0 to p.size: -pi log2 pi

	 */

	float Entropy(List<Float> proportions){

		float entropy = 0;

		for(float proportion : proportions){
			entropy += (-proportion)*Math.log(proportion)/Math.log(2);
		}

		return entropy;
	}

	/*
	 * Calculates the information gain for all attributes given a list of examples, and 
	 * returns them as a map of attributes to the float value of the gain
	 */
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
			for(int i = 0; i < attributes.size(); i++){

				AttributeValuePair newPair = new AttributeValuePair(attributes.get(i), Integer.parseInt(example.attributes.get(i)));

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

		// Calculate H(Label | AttributeValue): H(Y | X = v) = Sum of -Pr(Y = yi | X = v)log2(Pr(Y = yi | X = v))

		Map<String, Float> AttributeEntropy = new HashMap<String, Float>();

		for(Map.Entry<AttributeValuePair, Integer> labelEntry: AttributeValueFreqMap.entrySet()){
			if(attributes.contains(labelEntry.getKey().getAttribute())){
				AttributeValuePair newPair = labelEntry.getKey();
				
				// Get the proportions Pr(Y = yi | X = v)
				float HLAVSum = 0;
				List<Float> proportionsHYXv = new ArrayList<Float>();
				for(int i = 0; i < LabelAttributeValueMap.size(); i++){
					if(LabelAttributeValueMap.get(i).containsKey(newPair)){

						float propOfYiXv = (float) 
								(LabelAttributeValueMap.get(i).get(newPair)/(float)labelEntry.getValue());

						proportionsHYXv.add(propOfYiXv);	
					}
				}

				if(AttributeEntropy.get(newPair.getAttribute()) == null){
					AttributeEntropy.put(newPair.getAttribute(), (float) 0);
				}

				// Sum the proportions Pr(Y = yi | X = v)
				HLAVSum = Entropy(proportionsHYXv);
				
				// Pr(X = v)
				float Xv = (float) labelEntry.getValue()/ (float) examples.size();
				// Pr(X = v) * H(Y | X = v)
				float HYXpart = HLAVSum*Xv;
				
				// H(Y | X)
				float HYXSum = AttributeEntropy.get(newPair.getAttribute());

				// Successive summations by polling 
				//the map value and adding the new part
				AttributeEntropy.put(newPair.getAttribute(),
						(HYXSum == 0) ? (HYXpart) : (HYXSum + HYXpart));
			}

		}

		// Calculates and stores the final information gain
		Map<String, Float> IGMap = new HashMap<String, Float>();
		for(Map.Entry<String, Float> entry : AttributeEntropy.entrySet()){
			IGMap.put(entry.getKey(), HLabel - entry.getValue());
		}

		return IGMap;  
	}


	DecTreeNode BuildTree(List<Instance> examples, List<String> attributes, String defaultLabel, String parentAttribute){

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
			return new DecTreeNode(defaultLabel, null, parentAttribute, true);

		}else if(sameLabel){
			// return terminal node with label shared by all examples in tree
			return new DecTreeNode(unanimousLabel, null, parentAttribute, true);

		}else if(attributes.isEmpty()){
			// return terminal node with plurality value of current example labels
			return new DecTreeNode(MajorityLabel(examples), null, parentAttribute, true);

		}else{
			// importantAttribute = argmax for attribute a of IMPORTANCE(a, examples),
			Map<String, Float> IGMap = InformationGain(examples);

			double max = -1;
			String importantAttribute = null;

			for(Map.Entry<String, Float> IGEntry: IGMap.entrySet()){
				if(IGEntry.getValue() > max && attributes.contains(IGEntry.getKey())){

					importantAttribute = IGEntry.getKey();
					max = IGEntry.getValue();
				}
			}

			// calculate majorityLabel
			String majorityLabel = MajorityLabel(examples);

			// tree = new decision tree with root node A
			DecTreeNode treeToReturn = new DecTreeNode(majorityLabel, importantAttribute, parentAttribute, false);

			// create subtrees for each attribute value
			for(String value : attributeValues.get(importantAttribute)){

				// Get subset of examples with importantAttribute == value
				List<Instance> exs = new ArrayList<Instance>();

				for(Instance example: examples){
					if(example.attributes.get(getAttributeIndex(importantAttribute)).equals(value)){
						exs.add(example);
					}
				}

				// Pass attributes minus the one being used to create children
				List<String> childAttributes = new ArrayList<String>();

				for(String attribute: attributes){
					if(!attribute.equals(importantAttribute)){
						childAttributes.add(attribute);
					}
				}

				// Build subtree
				DecTreeNode childTree = BuildTree(exs, childAttributes, majorityLabel, value);

				// Add arc from tree to subtree
				treeToReturn.addChild(childTree);
			}

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
		String majorityLabel = MajorityLabel(train.instances);
		this.root = BuildTree(train.instances, train.attributes, majorityLabel, null);
		nonLeafNodes = new ArrayList<DecTreeNode>();
		flattenTree(root);
		Prune(this.root, tune);

	}

	void Prune(DecTreeNode root, DataSet tune){

		for(DecTreeNode testNode : nonLeafNodes){
			double accuracyWith = accuracy(tune);
			testNode.terminal = true;
			double accuracyWithout = accuracy(tune);
			testNode.terminal = false;
			if(accuracyWithout >= accuracyWith){
				testNode.terminal = true;
			}
		}
	}

	private List<DecTreeNode> nonLeafNodes;

	/*
	 * Makes a list out of the tree
	 */
	void flattenTree(DecTreeNode root){
		if(!root.terminal) nonLeafNodes.add(root);
		else return;
		for(DecTreeNode node : root.children){
			flattenTree(node);
		}
	}

	/*
	 * Checks the accuracy of the decision tree
	 */
	private double accuracy(DataSet tune){
		int correctCount = 0;
		for(Instance tuner : tune.instances){
			// If classified label equals tune label increment correct count
			if(classify(tuner).equals(tuner.label)) correctCount++;
		}
		return ((double)correctCount) / ((double)tune.instances.size());
	}

	@Override
	public String classify(Instance instance) {

		DecTreeNode traversalNode = root;
		while(!traversalNode.terminal){
			for(DecTreeNode child : traversalNode.children){
				if(instance.attributes.get(getAttributeIndex(traversalNode.attribute)).equals(child.parentAttributeValue)){
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

		Map<String, Float> IGMap = InformationGain(train.instances);
		
		for(int i = 0; i < IGMap.size(); i++){
			IGMap.get(attributes.get(i));
			System.out.format("%s %.5f\n", attributes.get(i), IGMap.get(attributes.get(i)));
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
			value = attributeValues
					.get(parent.attribute).
					get(attributeValueIndex);
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
