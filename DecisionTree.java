
import java.util.*;
import java.io.*;


public class DecisionTree {

	public static int NUM_CLASSES = 2;
	public static int NUM_ATTRIBUTE_VALUES = 3;
	public static int[] trainingClasses;
	public static String[] trainingAttributesNames;
	public static int[][] trainingAttributesValues;
	public static int[] testingClasses;
	public static String[] testingAttributesNames;
	public static int[][] testingAttributesValues;
	public static int modeFlag;
	public static TreeNode<String> globalTree;

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {

		if (args.length != 3) {
			System.out.println("Please enter three arguments in the format" +
			" <modeFlag> <trainFilename> <testFilename>");
			System.exit(0);

		}

		modeFlag = Integer.parseInt((args[0])); //convert modeFlag arg into an int
		int[][] trainingExamples = parseInput(args[1]);
		String[] trainAttributesNames = trainingAttributesNames;
		int[][] trainAttributesValues = trainingAttributesValues;
		int[][] testingExamples = null;
		String[] testAttributesNames = null;
		int[][] testAttributesValues = null;
		int trainingMajority = majority(trainingExamples, trainAttributesNames);
		if (modeFlag == 2) { //only parse testing set if modeFlag==2
			testingExamples = parseInput(args[2]);
			testAttributesNames = testingAttributesNames;
			testAttributesValues = testingAttributesValues;
		}
		
		TreeNode<String> tree = new TreeNode<String>(null);
		buildTree(trainingExamples, trainAttributesNames, trainAttributesValues, trainingMajority, tree, 0, "Root");
		tree = globalTree;
		
		
		if (modeFlag == 0) { //Print out the infoGain at the Root
			int numClass1 = 0;
			int numClass2 = 0;
			double hClass = 0;
			double[] info = new double[trainAttributesNames.length];
			for (int ROW = 0; ROW < trainingExamples.length; ROW++) {
				if (trainingExamples[ROW][trainAttributesNames.length] == trainingClasses[0]) {
					numClass1++;
				}
				else {
					numClass2++;
				}
			}
			hClass = entropy(numClass1, numClass2, trainingExamples.length);
			for (int i = 0; i < info.length; i++) {
				info[i] = infoGain(trainingExamples, hClass, i, trainAttributesNames, trainAttributesValues);
				System.out.println(trainAttributesNames[i] + " " + info[i]);
			}
		}
		
		

		if (modeFlag == 2) { //Runs through testingSet and classifies each example
			TreeNode<String> temp = tree; //don't want to lose where the tree starts
			boolean nextRow = false;
			for (int ROW = 0; ROW < testingExamples.length; ROW++) {
				temp = tree;
				nextRow = false;//Set back to false to get into while loop
				while (!nextRow) { //while you're still calculating the current example
					String question = temp.getData();
					
					int indexOfAttribute = -1; //gets overwritten by 
					for (int i = 0; i < testAttributesNames.length; i++) {
						if (testAttributesNames[i].equals(question)) {
							indexOfAttribute = i; //find the index corresponding to the current question
						}
					}
					if (indexOfAttribute == -1) { //the case where you are at a leaf node.
						testingExamples[ROW][testAttributesNames.length] = Integer.parseInt(temp.getData());
						nextRow = true;
					}
					if (!nextRow) { //If not a leaf node proceed to next node (left/middle/right)
						if (testingExamples[ROW][indexOfAttribute] == testAttributesValues[indexOfAttribute][0]) {
							temp = temp.getLeft();
						}
						if (testingExamples[ROW][indexOfAttribute] == testAttributesValues[indexOfAttribute][1]) {
							temp = temp.getMiddle();
						}
						if (testingExamples[ROW][indexOfAttribute] == testAttributesValues[indexOfAttribute][2]) {
							temp = temp.getRight();
						}
					}	
				}	
			}
			//For loop prints out all the classifications of each example
			for (int ROW = 0; ROW < testingExamples.length; ROW++) {
				System.out.println(testingExamples[ROW][testAttributesNames.length]);
			}
		}


	}

	
	/**
	 * This method creates a decision tree from a list of training Examples. It creates each
	 * node based on the greatest information gain associated with that attribute.
	 *
	 * @param (int[][] examples) (holds all the training examples)
	 * @param (String[] attributesNames) (holds the list of attributes names)
	 * @param (int[][] attributesValues) (each row corresponds to the possible values for that attribute)
	 * @param (int majority) (majority classifier over parents examples)
	 * @param (Tree<String> tree) (tree data structure that holds TreeNode objects)
	 * @param (int numIndent) (the number indents you should do based on current depth)
	 * @param (String lefMidRight) (for printing "1" "2" or "3" depending on current position
	 * 
	 */
	public static int buildTree (int[][] examples, String[] attributesNames, int[][] attributesValues,
			int majority, TreeNode<String> tree, int numIndent, String leftMidRight) {
		double bestAttribute;
		if (examples.length == 0) { //if you run out of examples, return majority class over parent examples
			tree.setData(Integer.toString(majority));
			if (modeFlag == 1) { //only print if modeFlag is set to 1
				for (int i = 0; i < numIndent; i++) {
					System.out.print('\t');
				}
				System.out.println(leftMidRight + " (" + tree.getData() + ")");
			}
			return majority;
		}
		else if (allSameClass(examples, attributesNames)) { //if all examples have same output, return that output
			tree.setData(Integer.toString(examples[0][attributesNames.length]));
			if (modeFlag == 1) { //only print if modeFlag is set to 1
				for (int i = 0; i < numIndent; i++) {
					System.out.print('\t');
				}
				System.out.println(leftMidRight + " (" + tree.getData() + ")");
			}
			return examples[0][attributesNames.length];
		}
		else if (attributesNames.length == 0) { //if no more attributes, return majority class over examples
			tree.setData(Integer.toString(majority(examples, attributesNames)));
			if (modeFlag == 1) { //only print if modeFlag is set to 1
				for (int i = 0; i < numIndent; i++) {
					System.out.print('\t');
				}
				System.out.println(leftMidRight + " (" + tree.getData() + ")");
			}
			return majority(examples, attributesNames);
		}
		else {
			bestAttribute = bestAttribute(examples, attributesNames, attributesValues);			
			tree.setData(attributesNames[(int) bestAttribute]); //add bestAttribute as next node
			if (modeFlag == 1) { //only print if modeFlag is set to 1
				for (int i = 0; i < numIndent; i++) {
					System.out.print('\t');
				}
				System.out.println(leftMidRight + " {" + tree.getData() + "?}");
			}

			int[][] newExamples1; //these 3 arrays hold the new examples
			int[][] newExamples2;
			int[][] newExamples3;
			int class1 = 0; //split examples array into 3 categories based on
			int class2 = 0; //the 3 different possible values of bestAttribute
			int class3 = 0;
			int[][] newAttributesValues = new int[attributesValues.length-1][attributesValues[0].length];
			String[] newAttributesNames = new String[attributesNames.length-1];

			/*decides how to partition the examples by putting them into categories based
			 * on what the value of bestAttribute is */
			for (int ROW = 0; ROW < examples.length; ROW++) { //bestAttribute=1, bestAttribute=2, bestAttribute=3
				if (examples[ROW][(int) bestAttribute] == attributesValues[(int) bestAttribute][0]) {
					class1++;
				}
				else if (examples[ROW][(int) bestAttribute] == attributesValues[(int) bestAttribute][1]) {
					class2++;
				}
				else if (examples[ROW][(int) bestAttribute] == attributesValues[(int) bestAttribute][2]) {
					class3++;
				}
			}

			/*These next for loops create new array with updated attribute values */
			for (int ROW = 0; ROW < bestAttribute; ROW++) {
				for (int COL = 0; COL < newAttributesValues[0].length; COL++) {
					newAttributesValues[ROW][COL] = attributesValues[ROW][COL];
				}
			}
			for (int ROW = (int) bestAttribute+1; ROW < attributesValues.length; ROW++) {
				for (int COL = 0; COL < newAttributesValues[0].length; COL++) {
					newAttributesValues[ROW-1][COL] = attributesValues[ROW][COL];
				}
			}
			/*These next for loops create new array with updated attribute Names*/
			for (int i = 0; i < bestAttribute; i++) {
				newAttributesNames[i] = attributesNames[i];
			}
			for (int i = (int) bestAttribute+1; i < attributesNames.length; i++) {
				newAttributesNames[i-1] = attributesNames[i];
			}
			//partition the examples into three new arrays
			newExamples1 = partitionExamples(examples, bestAttribute, attributesNames, class1, attributesValues[ (int) bestAttribute][0]);
			newExamples2 = partitionExamples(examples, bestAttribute, attributesNames, class2, attributesValues[ (int) bestAttribute][1]);
			newExamples3 = partitionExamples(examples, bestAttribute, attributesNames, class3, attributesValues[ (int) bestAttribute][2]);
			majority = majority(examples, attributesNames);
			
			String left = "1"; //These three strings are sent in through the code for printing purposes.
			String middle = "2";
			String right = "3";
			

			tree.setLeft(null); //This block of code adds a new node to the left (sets curr to left node as well)
			numIndent++;
			buildTree(newExamples1, newAttributesNames, newAttributesValues, majority, tree.getLeft(), numIndent, left);
			numIndent--;
			
			tree.setMiddle(null); //This block of code adds a new node to the middle (sets curr to middle node)
			numIndent++;
			buildTree(newExamples2, newAttributesNames, newAttributesValues, majority, tree.getMiddle(), numIndent, middle);
			numIndent--;
			
			tree.setRight(null); //This block of code adds a new node to the right (sets curr to right node)
			numIndent++;
			buildTree(newExamples3, newAttributesNames, newAttributesValues, majority, tree.getRight(), numIndent, right);
			numIndent--;
			
			globalTree = tree; //set tree to a global tree so you can use it later.


		}
		return 0;
	}

	
	/**
	 * This method runs through all the training examples and determines what
	 * the majority output is.
	 *
	 * @param (int[][] examples) (holds all the training examples)
	 * @param (String[] attributesNames) (holds the list of attributes names)
	 * 
	 */
	public static int majority(int[][] trainingExamples, String[] attributesNames) {
		int numClass1 = 0;
		int numClass2 = 0;
		int majority;
		for (int ROW = 0; ROW < trainingExamples.length; ROW ++) {
			if (trainingExamples[ROW][attributesNames.length] == trainingClasses[0]) {
				numClass1++;
			}
			else {
				numClass2++;
			}
		}
		if (numClass1 >= numClass2) { //if numClass1 >= numClass2 assign it as the majority
			majority = trainingClasses[0];
		}
		else { //otherwise numClass2 is the majority
			majority = trainingClasses[1];
		}
		return majority;
	}
	
	
	/**
	 * This method partitions a given training example array into a smaller subset based
	 * on (bestAttribute=x) where x is a specified value for bestAttribute
	 *
	 * @param (int[][] examples) (holds all the training examples)
	 * @param (String[] attributesNames) (holds the list of attributes names)
	 * @param (double bestAttribute) (holds a double value of the bestAttribute)
	 * @param (int numClass) (holds the length of the new partition examples that is returned
	 * @param (int attribValue) (holds the value of bestAttribute that you are partitioning for
	 * 
	 */
	public static int[][] partitionExamples(int[][] examples, double bestAttribute, String[] attributesNames, int numClass,
			int attribValue) {
		//Both these arrays have one less column because we don't need the bestAttribute column anymore
		int[][] newExamples = new int[examples.length][examples[0].length-1];
		int[][] newExamplesFinal = new int[numClass][examples[0].length-1];
		for (int ROW = 0; ROW < examples.length; ROW++) {
			for (int COL = 0; COL < bestAttribute; COL++) {
				newExamples[ROW][COL] = examples[ROW][COL];
			}
			for (int COL = (int) (bestAttribute+1); COL < attributesNames.length+1; COL++) {
				newExamples[ROW][COL-1] = examples[ROW][COL];
			}
		}
		int count = 0;
		for (int ROW = 0; ROW < examples.length; ROW++) {
			if (examples[ROW][(int) bestAttribute] == attribValue) { //Only want rows with desired attribValue
				for (int COL = 0; COL < newExamples[0].length; COL++) {
					newExamplesFinal[count][COL] = newExamples[ROW][COL];
				}
				count++;
			}
			
		}
		return newExamplesFinal;
	}
	
	
	/**
	 * This method calculates what the bestAttribute is by calling infoGain for each 
	 * attribute.
	 *
	 * @param (int[][] examples) (holds all the training examples)
	 * @param (String[] attributesNames) (holds the list of attributes names)
	 * @param (int[][] attributesValues) (each row corresponds to the possible values for that attribute)
	 * 
	 */
	public static double bestAttribute (int[][] examples, String[] attributesNames, int[][] attributesValues) {
		
		double hClass = 0;
		double numClass1 = 0;
		double numClass2 = 0;
		double[] info = new double[attributesNames.length];
		double greatestInfoGain = 0;
		double bestAttribute = 0;
		//The below code calculates the entropy of the entire set.
		for (int ROW = 0; ROW < examples.length; ROW++) {
			if (examples[ROW][attributesNames.length] == trainingClasses[0]) {
				numClass1++;
			}
			else {
				numClass2++;
			}
		}
		hClass = entropy(numClass1, numClass2, examples.length);
		for (int i = 0; i < info.length; i++) {
			info[i] = infoGain(examples, hClass, i, attributesNames, attributesValues); //Store all the info gains in array
			if (info[i] > greatestInfoGain) { //if the next info is >greatestInfoGain then reassign
				greatestInfoGain = info[i];
				bestAttribute = i;
			}
		}	
		
		return bestAttribute;
	}
	
	/**
	 * This method calculates the infoGain for a given attribute
	 *
	 * @param (int[][] examples) (holds all the training examples)
	 * @param (String[] attributesNames) (holds the list of attributes names)
	 * @param (double hClass) (holds the entropy of the entire examples array)
	 * @param (int attrib) (holds the int value of the attrib you are testing)
	 * @param (int[][] attributesValues) (holds the possible values of each attribute)
	 * 
	 */
	public static double infoGain (int[][] examples, double hClass, int attrib, String[] attributesNames,
			int[][] attributesValues) {
		double hAttribute = 0; //hold entropy of attribute
		double infoGain = 0;
		double numClass1 = 0;
		double numClass2 = 0;
		double numClass3 = 0;
		double[] numClassifier1 = new double[NUM_ATTRIBUTE_VALUES];
		double[] numClassifier2 = new double[NUM_ATTRIBUTE_VALUES];
		double temp;
		//This forloop goes through examples array and decides the distribution of 
		//of the class label for the specified attribute.
		for (int ROW = 0; ROW < examples.length; ROW++) {
			if (examples[ROW][attrib] == attributesValues[attrib][0]) {
				numClass1++;
				//for class1 of the specified attribute we decide how many are positive
				//and how many are negative
				if (examples[ROW][attributesNames.length] == trainingClasses[0]) {
					numClassifier1[0]++;
				}
				else {
					numClassifier2[0]++;
				}
			}
			if (examples[ROW][attrib] == attributesValues[attrib][1]) {
				//for class2 of the specified attribute we decide how many are positive
				//and how many are negative
				numClass2++;
				if (examples[ROW][attributesNames.length] == trainingClasses[0]) {
					numClassifier1[1]++;
				}
				else {
					numClassifier2[1]++;
				}
			}
			if (examples[ROW][attrib] == attributesValues[attrib][2]){
				//for class3 of the specified attribute we decide how many are positive
				//and how many are negative
				numClass3++;
				if (examples[ROW][attributesNames.length] == trainingClasses[0]) {
					numClassifier1[2]++;
				}
				else {
					numClassifier2[2]++;
				}
			}
			//TODO: figure out information gain
		}
		//hAttribute is storing the conditional entropy for the given attribute
		hAttribute = ((numClass1/examples.length) * entropy(numClassifier1[0], numClassifier2[0], numClass1) +
					 ((numClass2/examples.length) * entropy(numClassifier1[1], numClassifier2[1], numClass2) +
					 ((numClass3/examples.length) * entropy(numClassifier1[2], numClassifier2[2], numClass3))));
		infoGain = hClass - hAttribute; //Equation for infoGain
		
		return infoGain;
	}

	
	/**
	 * This method calculates entropy
	 *
	 * @param (double numClass1) (holds the number of class1)
	 * @param (double numClass2) (holds the number of class2)
	 * @param (double total) (number of examples the entropy is calculated over)
	 * @return (entropy for given inputs)
	 * 
	 */
	public static double entropy (double numClass1, double numClass2, double total) {
		double entropy = 0;
		if (total == 0) { //TODO: if total = 0 we will have a null case so set both these to 0
			numClass1 = 0;
			numClass1 = 0;
		}
		else {
			numClass1 = numClass1 / total;
			numClass2 = numClass2 / total;
		}
		if (numClass1 != 0) { //log(0) is undefined
			entropy += (numClass1) *(Math.log(numClass1) / Math.log(2));
		}
		if (numClass2 != 0) { //log(0) is undefined
			entropy += (numClass2) *(Math.log(numClass2) / Math.log(2));
		}
		entropy *= -1; //only want positive entropy
		
		return entropy;
	}
	
	/**
	 * This method determines if the whole examples array is classified
	 * the same
	 *
	 * @param (int[][] examples) (holds all the training examples)
	 * @param (String[] attributesNames) (holds the list of attributes names)
	 * @reutnr (boolean of whether the whole set is classified the same)
	 */
	public static boolean allSameClass (int[][] examples, String[] attributesNames) {
		int classifier  = examples[0][attributesNames.length];
		for (int ROW = 0; ROW < examples.length; ROW++) {
			if (examples[ROW][attributesNames.length] != classifier) {
				return false; //Found two classifiers that don't match.
			}
		}
		return true; //All the classes are the same
	}
	
	/**
	 * 
	 * This method parses the input file into various arrays.
	 * examples is a 2d array that holds all the examples plus their classifier
	 * classesInt is an array that holds the two different classifiers (usually 1 and 2)
	 * attributesNames is an array that holds the string names of the attributes
	 * attributesValues is an array that holds the possible values of each attribute
	 * 		in the form "1,2,3". Therefore you will have to parse later if used
	 * 
	 * @param (String fileName) is the name of the file you want to parse
	 * 
	 */
	public static int[][] parseInput (String fileName) throws FileNotFoundException {
		String[] parsedString;
		int[][] examples;
		String[] classesString = new String[NUM_CLASSES];
		int[] classesInt = new int[NUM_CLASSES];
		String[] attributesNames; //Holds attribute names such as "ClumpThickness"
		String[] attributesValues; //Holds possible values in form "1,2,3"
		int[][] attributesValuesInt; //Holds Attributes possible values;
		int numExamples = 0;
		int numAttributes = 0;
		String temp;
		String nextLine;
		File file = new File(fileName);
		Scanner in = null;
		in = new Scanner(file);
		
		//This while loop calculates the length of the examples
		while (in.hasNextLine()) {
			nextLine = in.nextLine();
			if (!nextLine.substring(0,2).equals("//") && !nextLine.substring(0,2).equals("%%")
					&& !nextLine.substring(0,2).equals("##")) {
				numExamples++;
			}
			if (nextLine.substring(0,1).equals("#")) {
				numAttributes++;
			}
		}
		examples = new int[numExamples][numAttributes + 1]; //+1 for the output
		attributesNames = new String[numAttributes];
		attributesValues = new String[numAttributes]; //Holds the values of attributes in form "1,2,3"
		attributesValuesInt = new int[numAttributes][NUM_ATTRIBUTE_VALUES]; //2D array that holds possible values
																			//for each attribute.
		//Create another scanner to run through examples again
		in = new Scanner(file);
		int attributeCount = 0; //Keep track of how many attributes have been added to attributesNames array
		int exampleCount = 0; //Keep track of how many examples you've added to the array.
		while (in.hasNextLine()) {
			nextLine = in.nextLine();
			parsedString = nextLine.split(" ");
			if (parsedString[0].substring(0,2).equals("//")) {
				//Do nothing because it's a comment
			}
			else if (parsedString[0].substring(0,2).equals("%%")) {
				temp = parsedString[0].substring(2);
				classesString = temp.split(",");
				for (int i = 0; i < classesString.length; i++) {
					classesInt[i] = Integer.parseInt(classesString[i]); //store classifiers
				}
			}
			else if (parsedString[0].substring(0,2).equals("##")) {
				temp = parsedString[0].substring(2);
				attributesNames[attributeCount] = temp;
				attributesValues[attributeCount] = parsedString[1];
				attributeCount++;	
			}
			else { //The case where we've hit the actual examples
				parsedString = nextLine.split(","); 
				for (int i = 0; i < parsedString.length; i++) {
					if (isInteger(parsedString[i])) {
						examples[exampleCount][i] = Integer.parseInt(parsedString[i]);
					}
				}
				exampleCount++;
			}
		}
		int tempInt = 0; //The following double for-loop parses the possible attribute values
						 //into a 2D array so that each row corresponds to a different attribute.
		for (int i = 0; i < attributesValues.length; i++) {
			parsedString = attributesValues[i].split(",");
			for (int j = 0; j < parsedString.length; j++) {
				attributesValuesInt[i][j] = Integer.parseInt(parsedString[j]);
			}
		}
		if (fileName.equals("train.txt")) {
			trainingClasses = classesInt; //Can't have 4 return statements so assign these to Globals
			trainingAttributesNames = attributesNames;
			trainingAttributesValues = attributesValuesInt;
		}
		else if (fileName.equals("test.txt")) {
			testingClasses = classesInt;
			testingAttributesNames = attributesNames;
			testingAttributesValues = attributesValuesInt;
		}
		
		
		return examples;
	}

	/**
	 * Returns whether or not a given string is an integer
	 * 
	 * @param (String s)
	 */
	public static boolean isInteger(String s) {
		try { 
			Integer.parseInt(s); 
		} catch(NumberFormatException e) { 
		return false; 
		}
		// only got here if we didn't return false
		return true;
	}

}
