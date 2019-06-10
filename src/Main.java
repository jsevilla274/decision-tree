public class Main
{
    public String[][] database = {
            {"youth", "high", "no", "fair", "no"},
            {"youth", "high", "no", "excellent", "no"},
            {"middle_aged", "high", "no", "fair", "yes"},
            {"senior", "medium", "no", "fair", "yes"},
            {"senior", "low", "yes", "fair", "yes"},
            {"senior", "low", "yes", "excellent", "no"},
            {"middle_aged", "low", "yes", "excellent", "yes"},
            {"youth", "medium", "no", "fair", "no"},
            {"youth", "low", "yes", "fair", "yes"},
            {"senior", "medium", "yes", "fair", "yes"},
            {"youth", "medium", "yes", "excellent", "yes"},
            {"middle_aged", "medium", "no", "excellent", "yes"},
            {"middle_aged", "high", "yes", "fair", "yes"},
            {"senior", "medium", "no", "excellent", "no"}
    };

    public String[] attributeList = {"age", "income", "student", "credit_rating"};

    public static void main(String[] args)
    {
        new Main();
    }

    public Main()
    {
        //create "trained" decision tree
        DecisionTree tree = new DecisionTree(database, attributeList);
        
        // For testing, delete all
        for (String tuple[] : database) {
            String withoutClassLabel[] = {tuple[0], tuple[1], tuple[2], tuple[3]};
            System.out.print("Finding label for tuple: ");
            for (String attr : withoutClassLabel)
                System.out.print(attr + ", ");
            System.out.println();
            System.out.println("Infered class label -> " + tree.classifyTuple(withoutClassLabel, attributeList));
            System.out.println();
        }                
        //

        // PART 3: Implement user interaction
    }
    
    
}
