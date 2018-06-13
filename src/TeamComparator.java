import java.util.Comparator;

/**
 * Sorts Teams by their number.
 * @author Finn Frankis
 * @version 6/12/18
 */
public class TeamComparator implements Comparator<Team>
{
    /**
     * Compares the two teams using the standard compareTo() function.
     */
    public int compare(Team t1, Team t2)
    {
        return t1.compareTo(t2);
    }

}
