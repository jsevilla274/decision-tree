public class Node
{
    private String info; //either splitting attr or class
    public Node[] branches;
    public String[] branchNames;

    public Node() {}

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String i)
    {
        info = i;
    }

    public boolean isLeaf()
    {
        return branches == null && branchNames == null;
    }
}
