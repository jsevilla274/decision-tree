import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DecisionTree
{
    private Node root;

    public DecisionTree(String[][] database, String[] attributeList)
    {
        root = new Node();
        generate(root, database, attributeList);
    }

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

        // (10) create a partition for each branch
        String[][] partition;
        for (int i = 0; i < node.branchNames.length; i++)
        {
            partition = partitionByValue(database, splittingAttr, node.branchNames[i]);

            //printPartition(partition, "value", node.branchNames[i]); // dev test

            // (12) assume: partition is not empty
            // (14) "attach" node through new generation
            node.branches[i] = new Node();
            generate(node.branches[i], partition, attributeList); // "new String[0]" forces end at 1st level
        }
    }

    // for testing only
    private void printPartition(String[][] database, String splittingAttr, String value)
    {
        System.out.println("\n" + splittingAttr + ": " + value);
        for (int i = 0; i < database.length; i++)
        {
            for (int j = 0; j < database[0].length; j++)
            {
                System.out.print(database[i][j] + " ");
            }
            System.out.println();
        }
    }

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

    private String[] findValues(String[][] database, int splittingAttr)
    {
        ArrayList<String> splittingValues = new ArrayList<>();
        for (int i = 0; i < database.length; i++)
        {
            if (!splittingValues.contains(database[i][splittingAttr]))
            {
                splittingValues.add(database[i][splittingAttr]);
            }
        }

        return splittingValues.toArray(new String[0]);
    }

    private String[][] partitionByValue(String[][] database, int splittingAttr, String value)
    {
        String[][] partition = new String[database.length][(database[0].length - 1)]; // reduced horizontally to account for 1 less attr
        int tuples = 0;
        int dbCount;
        for (int i = 0; i < database.length; i++)
        {
            // if tuple has specified value in attribute
            if (database[i][splittingAttr].equals(value))
            {
                // insert the tuple into the partition
                dbCount = 0;
                for (int j = 0; j < database[0].length - 1; j++)
                {
                    if (dbCount == splittingAttr)
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

        // return partition
        return Arrays.copyOf(partition, tuples);
    }

    /*
        PART 2
        Dev notes: The only special consideration here is make sure you return
        the INDEX of the attribute (from attributeList) that we should split by
        (i.e. if age, then 0. if income, then 1)

        Be sure you use Information Gain
     */
    
    private int splittingCriterion(String[][] database, String[] attributeList)
    {
        testPrint(database, attributeList);                                             //  <-  debugging
        
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
        System.out.println("Splitting attribute chosen: " + bestAttribute + "\n");      // <-  debugging
        
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
    
    // For debugging: to delete later
    private void testPrint(String[][] database, String[] attributeList)                // <-  debugging
    {
        for (int i = 0; i < attributeList.length; i++) {
            System.out.print(attributeList[i].toUpperCase() + ", ");
        }
        System.out.print("CLASS LABEL");
        System.out.println();
        for (int y = 0; y < database.length; y++) {
            for (int x = 0; x < database[0].length; x++) {
                System.out.print(database[y][x] + ", ");
            }
            System.out.println();
        }
        
    }

    /*
        PART 3
        Dev notes: After initialization of this class, the tree is created.
        The only thing you must do is traverse the decision tree through the
        "root" node.

        Note each branch from a node has a corresponding node in the .branch array
        and a value in .branchNames array (logically connected by indexes)

        return: the correct class (in this case "yes" or "no")
     */
    public String classifyTuple(String[] tuple)
    {
        return "";
    }
}
