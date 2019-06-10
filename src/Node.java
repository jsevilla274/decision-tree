/**
 * Node.java
 *
 * Holds information of an individual node in a larger decision tree.
 */
public class Node
{
    private String info;
    public Node[] branches; // array of children nodes
    public String[] branchNames; // name(s) of the branch values by which the node splits

    public Node() {}

    /**
     * Retrieves the information of the node. If the node is a leaf, then it
     * returns a class label, else it returns the name of the attribute the
     * node splits by.
     *
     * @return the class label OR splitting attribute
     */
    public String getInfo()
    {
        return info;
    }

    /**
     * Sets the information held in the node.
     *
     * @param i the class label OR splitting attribute to assign
     * @see getInfo()
     */
    public void setInfo(String i)
    {
        info = i;
    }

    public boolean isLeaf()
    {
        return branches == null && branchNames == null;
    }
}
