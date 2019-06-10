/**
 * Program that tests the implementation of a decision tree algorithm that
 * stops at the first level of induction after the root. Testing is carried out
 * by asking a user to input a tuple of a specified format, which is then classified
 * and returned to the user with the correct class label
 */
import java.util.Scanner;

public class Main
{
    /*
        The database is formatted as follows:
        Tup1: {val1, val2, ..., valN, classLabel}
        Tup2: {val1, val2, ..., valN, classLabel}
        .
        .
        .
        TupN: {val1, val2, ..., valN, classLabel}

     */
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

    // Attribute list formatted as follows: {attr1, attr2, ..., attrN}
    public String[] attributeList = {"age", "income", "student", "credit_rating"};

    public static void main(String[] args)
    {
        new Main();
    }

    public Main()
    {
        //create "trained" decision tree, outputs 1st level (like Figure 8.5)
        DecisionTree tree = new DecisionTree(database, attributeList);

        //prompt user for a tuple
        String[] tuple = inputTuple();
        System.out.println("The label of this tuple for class:buys_computer is \"" + tree.classifyTuple(tuple) + "\"");
    }

    private String[] inputTuple()
    {
        String[] tuple = new String[4];
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter age (youth, middle_aged, senior): ");
        tuple[0] = sc.next();
        if (!tuple[0].equals("youth") && !tuple[0].equals("middle_aged") && !tuple[0].equals("senior"))
        {
            System.out.println(tuple[0] + " is not a valid value for age");
            System.exit(1);
        }

        System.out.println("Enter income (low, medium, high): ");
        tuple[1] = sc.next();
        if (!tuple[1].equals("low") && !tuple[1].equals("medium") && !tuple[1].equals("high"))
        {
            System.out.println(tuple[1] + " is not a value for income");
            System.exit(1);
        }

        System.out.println("Enter student status (yes, no): ");
        tuple[2] = sc.next();
        if (!tuple[2].equals("yes") && !tuple[2].equals("no"))
        {
            System.out.println(tuple[2] + " is not a value for student status");
            System.exit(1);
        }

        System.out.println("Enter credit rating (excellent, fair): ");
        tuple[3] = sc.next();
        if (!tuple[3].equals("excellent") && !tuple[3].equals("fair"))
        {
            System.out.println(tuple[3] + " is not a value for credit rating");
            System.exit(1);
        }

        return tuple;
    }
}
