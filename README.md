CS540-DecisionTree
==================

This program creates a decision tree that determines if a person has cancer or not based on given symptoms.

The program demonstrates an effective method of machine learning, more information on that here:
http://en.wikipedia.org/wiki/Decision_tree_learning.

*DecisionTree.java contains the main class and the bulk of the algorithm.
*TreeNode.java is a custom general Tree data structure that is parameterized to allow it to hold any type of data.

You can use the DecisionTree.jar file to run it.
  *On windows navigate to the correct folder and run: java -jar DecisionTree.jar <modeflag> train.txt test.txt
  where the modeflag is 0-2
    *modeflag=0 will display the information gain at the root of the tree (the more information gain the better)
    *modeflag=1 will display the entire tree where child nodes will have the number '1' or '2' where 1 means
                the cancer is benign and 2 means the cancer is malignant.
    *modeflag=2 will display just the final classified testing cases (either benign or malignant)
