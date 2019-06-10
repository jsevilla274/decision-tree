/**
 * DecisionTree.java
 *
 * Takes in a database of training tuples with class labels and its associated
 * attribute list and generates a trained decision tree.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DecisionTree
{
    private Node root;                  // root of the decision tree
    private final String[] ATTR_LIST;   // the initial attribute list

    public DecisionTree(String[][] database, String[] attributeList)
    {
        ATTR_LIST = attributeList;
        root = new Node();
        generate(root, database, attributeList);
    }

    /**
     * Recursively generates a trained decision tree.
     *
     * NOTE: This partial implementation of the decision tree only renders the
     * "first" level of the decision tree, and derives a class label through
     * the "majority class" method.
     *
     * "DISPLAYING" code may be removed, and "assumptions" may be implemented
     * for further development.
     *
     * @param node the current tree node
     * @param database A 2-D array of n-attribute training tuples with
     *                 associated class labels as the n+1th element
     * @param attributeList A list of attribute names associated with training tuples
     */
    private void generate(Node node, String[][] database, String[] attributeList)
    {
        attributeList = Arrays.copyOf(attributeList, attributeList.length); // deep copy

        // (2) assume: if tuples in database are same class C, return class C

        // (4) if attribute list is empty then
        if (attributeList.length == 0)
        {
            node.setInfo(majorityClass(database));
            return;
        }

        // (6) find best splitting criterion
        int splittingAttr = splittingCriterion(database, attributeList);
        
        // (7) label node N with splitting criterion
        node.setInfo(attributeList[splittingAttr]);

        // (8) assume: splitting attribute is discrete-valued and multiway splits allowed

        // (9) remove splitting attribute from attrList
        for (int i = 0; i < attributeList.length; i++)
        {
            if (i >= splittingAttr && i < attributeList.length - 1)
            {
                attributeList[i] = attributeList[i + 1];
            }
        }
        attributeList = Arrays.copyOf(attributeList, attributeList.length - 1);

        // (10) find values of splittingAttr in db and create branches for them
        node.branchNames = findValues(database, splittingAttr);
        node.branches = new Node[node.branchNames.length];

        // DISPLAYING splitting attribute of frist level
        System.out.println("\nsplitting by attribute: " + node.getInfo() + "\n");

        // (10) create a partition for each branch
        String[][] partition;
        for (int i = 0; i < node.branchNames.length; i++)
        {
            partition = partitionByValue(database, splittingAttr, node.branchNames[i]);

            // DISPLAYING tuples of first level
            displayLV1Partition(partition, attributeList, node.getInfo(), node.branchNames[i]);

            // (12) assume: partition is not empty
            // (14) "attach" node through new generation
            node.branches[i] = new Node();
            generate(node.branches[i], partition, new String[0]); // "new String[0]" forces end at 1st level
        }
    }

    /**
     * Displays a partition of the first level of the tree.
     *
     * @param database a given partition of the db
     * @param attrList attributes associated with partition
     * @param splittingAttr the attribute by which the partitioning was done
     * @param splittingValue the attribute value of the branch
     */
    private void displayLV1Partition(String[][] database, String[] attrList,
                                     String splittingAttr, String splittingValue)
    {
        System.out.println(splittingAttr + ": " + splittingValue);
        String[] tuple;

        // appending "class" to attrList
        attrList = Arrays.copyOf(attrList, attrList.length + 1);
        attrList[attrList.length - 1] = "class";

        // printing tuples
        for (int i = -1; i < database.length; i++)
        {
            tuple = (i == -1) ? attrList : database[i];

            for (String value: tuple)
            {
                System.out.printf("%-16s", value);
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Finds the majority class label of the tuples in a given database.
     *
     * @param database A 2-D array of n-attribute training tuples with
     *                 associated class labels as the n+1th element
     * @return the class label
     */
    private String majorityClass(String[][] database)
    {
        // get class "values"
        int classIndex = database[0].length - 1;
        String[] classes = findValues(database, classIndex);
        int[] classCounts = new int[classes.length];

        // for each class i
        for (int i = 0; i < classCounts.length; i++)
        {
            // and each tuple j in the db
            for (int j = 0; j < database.length; j++)
            {
                // if the jth tuple's class equals the ith class
                if (database[j][classIndex].equals(classes[i]))
                {
                    // increment that class's count
                    classCounts[i]++;
                }
            }
        }

        // find the maximum of the classes
        int maxClassIndex = 0;
        for (int i = 1; i < classCounts.length; i++)
        {
            if (classCounts[maxClassIndex] < classCounts[i])
            {
                maxClassIndex = i;
            }
        }
        return classes[maxClassIndex];
    }

    /**
     * Finds the values of an attribute from tuples in a given database.
     *
     * @param database A 2-D array of n-attribute training tuples with
     *                 associated class labels as the n+1th element
     * @param attrIndex the index of the attribute whose values must be discovered
     *
     * @return a list of values
     */
    private String[] findValues(String[][] database, int attrIndex)
    {
        ArrayList<String> splittingValues = new ArrayList<>();
        for (int tupleIndex = 0; tupleIndex < database.length; tupleIndex++)
        {
            if (!splittingValues.contains(database[tupleIndex][attrIndex]))
            {
                splittingValues.add(database[tupleIndex][attrIndex]);
            }
        }

        return splittingValues.toArray(new String[0]);
    }

    /**
     * Partitions a database by a given attribute value
     *
     * @param database A 2-D array of n-attribute training tuples with
     *                 associated class labels as the n+1th element
     * @param attrIndex the index of the attribute
     * @param value the string value of the attribute
     *
     * @return a partition of similarly-valued tuples
     */
    private String[][] partitionByValue(String[][] database, int attrIndex, String value)
    {
        int attributes = database[0].length - 1;
        String[][] partition = new String[database.length][attributes];
        int tuples = 0;
        int dbCount;
        for (int i = 0; i < database.length; i++)
        {
            // if tuple has specified value in attribute
            if (database[i][attrIndex].equals(value))
            {
                // insert the tuple into the partition
                dbCount = 0;
                for (int j = 0; j < attributes; j++)
                {
                    if (dbCount == attrIndex)
                    {
                        j--; //skip splittingAttr
                    }
                    else
                    {
                        partition[tuples][j] = database[i][dbCount];
                    }
                    dbCount++;
                }
                tuples++;
            }
        }

        return Arrays.copyOf(partition, tuples);
    }

    /**
     * Finds the most suitable attribute by which to split a given database using
     * Information Gain.
     *
     * @param database A 2-D array of n-attribute training tuples with
     *                 associated class labels as the n+1th element
     * @param attributeList A list of attribute names associated with training tuples
     *
     * @return the index of the best attribute by which to partition
     */
    private int splittingCriterion(String[][] database, String[] attributeList)
    {
        String[] classLabels = findValues(database, database[0].length - 1);
        double infoD = getInfo(database, classLabels);
        
        double highestGain = 0;
        int bestAttribute = 0;
        
        for (int currentAttr = 0; currentAttr < attributeList.length; currentAttr++) {
            double infoAttr = getInfo(database, currentAttr, classLabels);
            double gain = infoD - infoAttr; 
            
            if (gain > highestGain) {
                highestGain = gain;
                bestAttribute = currentAttr;
            }
        }
        
        return bestAttribute;
    }
    
    private double getInfo(String[][] database, String[] classLabels) 
    {
        final int classLabelIndex = database[0].length - 1;
        final int totalCount = database.length; 
        
        int frequency[] = new int[classLabels.length];

        for (String[] tuple : database) {
            for (int i = 0; i < classLabels.length; i++) {
                if (tuple[classLabelIndex].equals(classLabels[i])) {
                    frequency[i]++;
                    break;
                }
            }
        }
        
        double info = 0.0;
        for (int current : frequency) {
            final double proportion = current / (double) totalCount;
            if (proportion > 0) {
                info += -1 * proportion * Math.log(proportion) / Math.log(2);
            }
        }
        return info;
    }
    
    private double getInfo(String[][] database, int attrIndex, String[] classLabels) 
    {    
        final int classLabelIndex = database[0].length - 1;
        final int totalCount = database.length; 
        
        final String attrValues[] = findValues(database, attrIndex);
        int frequencyTable[][] = new int[attrValues.length][classLabels.length];
        
        // Associate attribute value string to an index
        Map <String,Integer> attrToIndex = new HashMap<>();
        for (int i = 0; i < attrValues.length; i++) {
            attrToIndex.put(attrValues[i], i);
        }
        
        // Associate classLabel string to an index
        Map <String,Integer> classToIndex = new HashMap<>();
        for (int i = 0; i < classLabels.length; i++) {
            classToIndex.put(classLabels[i], i);
        }
        
        // Populate frequency table
        for (String[] tuple : database) {
            final String attributeValue = tuple[attrIndex];
            final String classLabel = tuple[classLabelIndex];
            frequencyTable[attrToIndex.get(attributeValue)][classToIndex.get(classLabel)]++;
        }
        
        double info = 0.0;
        
        // Calculate info from frequency table
        for (int current[] : frequencyTable) {
            int sum = 0;
            for (int e : current) {
                sum += e;
            }
            
            double A = sum / (double) totalCount;
            double B = 0.0;
      
            for (int i = 0; i < classLabels.length; i++) {
                final double proportion = current[i] / (double) sum;
                if (proportion > 0) {
                    B += -1 * proportion * Math.log(proportion) / Math.log(2);
                }
            }
            info += A * B;
        }
        
        return info;
    }

    /**
     * Classifies a given tuples using the trained decision tree.
     *
     * @param tuple an input tuple
     * @return the class label of the input
     */
    public String classifyTuple(String[] tuple)
    {
        Node curr = root;

        while (!curr.isLeaf())
        {
            // finds the attribute being tested
            for (int i = 0; i < ATTR_LIST.length; i++)
            {
                if (curr.getInfo().equals(ATTR_LIST[i]))
                {
                    // finds the branch to take
                    for (int j = 0; j < curr.branchNames.length; j++)
                    {
                        if (curr.branchNames[j].equals(tuple[i]))
                        {
                            curr = curr.branches[j];
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return curr.getInfo();
    }
}
