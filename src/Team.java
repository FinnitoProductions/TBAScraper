import java.util.ArrayList;
import java.util.Map;

/**
 * Represents an FRC Robotics Team.
 * @author Finn Frankis
 * @version 6/11/18
 */
public class Team
{
    private String name;
    private int number,
                rookieYear;
    private Map<Integer, Integer> totalMatchLosses, 
                               totalMatchTies, 
                               numCompetitions,
                               numWins,
                               numChairmanWins;
    private Map<Integer, Double> totalMatchWins;
    private boolean active;
    private ArrayList<Integer> activeYears;
    
    /**
     * Constructs a new team.
     * @param name the name of the team
     * @param totalMatchWins how many matches the team has won
     * @param totalMatchLosses how many matches the team has lost
     * @param totalMatchTies how many matches the team has tied
     * @param numChairmanWins how many chairmans awards the team has won
     * @param rookieYear the first year the team played in FRC
     * @param numCompetitions the number of competitions in which the team has played
     * @param active whether the team is currently active
     * @number the team's number
     */
    public Team(String name, int number, int rookieYear, Map<Integer, Double> totalMatchWins,
            Map<Integer, Integer> totalMatchLosses, Map<Integer, Integer> totalMatchTies, Map<Integer, Integer> numChairmanWins,
            Map<Integer, Integer> numCompetitions, Map<Integer, Integer> numWins, boolean active, ArrayList<Integer> activeYears)
    {
        super();
        this.name = name;
        this.number = number;
        this.rookieYear = rookieYear;
        this.totalMatchWins = totalMatchWins;
        this.totalMatchLosses = totalMatchLosses;
        this.totalMatchTies = totalMatchTies;
        this.numCompetitions = numCompetitions;
        this.numWins = numWins;
        this.active = active;
        this.numChairmanWins = numChairmanWins;
        this.activeYears = activeYears;
    }
    
    public ArrayList<Integer> getActiveYears()
    {
        return activeYears;
    }
    
    public Map<Integer, Integer> getNumChairmanWins()
    {
        return numChairmanWins;
    }

    public void setNumChairmanWins(Map<Integer, Integer> numChairmanWins)
    {
        this.numChairmanWins = numChairmanWins;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public int getRookieYear()
    {
        return rookieYear;
    }

    public void setRookieYear(int rookieYear)
    {
        this.rookieYear = rookieYear;
    }


    public Map<Integer, Double> getTotalMatchWins()
    {
        return totalMatchWins;
    }

    public void setTotalMatchWins(Map<Integer, Double> totalMatchWins)
    {
        this.totalMatchWins = totalMatchWins;
    }

    public Map<Integer, Integer> getTotalMatchLosses()
    {
        return totalMatchLosses;
    }

    public void setTotalMatchLosses(Map<Integer, Integer> totalMatchLosses)
    {
        this.totalMatchLosses = totalMatchLosses;
    }

    public Map<Integer, Integer> getTotalMatchTies()
    {
        return totalMatchTies;
    }

    public void setTotalMatchTies(Map<Integer, Integer> totalMatchTies)
    {
        this.totalMatchTies = totalMatchTies;
    }

    public Map<Integer, Integer> getNumCompetitions()
    {
        return numCompetitions;
    }

    public void setNumCompetitions(Map<Integer, Integer> numCompetitions)
    {
        this.numCompetitions = numCompetitions;
    }

    public Map<Integer, Integer> getNumWins()
    {
        return numWins;
    }

    public void setNumWins(Map<Integer, Integer> numWins)
    {
        this.numWins = numWins;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    @Override
    public String toString()
    {
        return "Team [name=" + name + ", number=" + number + ", rookieYear=" + rookieYear 
                + ", totalMatchWins=" + totalMatchWins + ", totalMatchLosses=" + totalMatchLosses + ", totalMatchTies="
                + totalMatchTies + ", numCompetitions=" + numCompetitions + ", numWins=" + numWins
                + ", numChairmanWins=" + numChairmanWins + ", active=" + active + "]";
    }
    
    public int compareTo(Team t)
    {
        return getNumber() - t.getNumber();
    }

    
    
    

}
