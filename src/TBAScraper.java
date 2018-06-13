import java.util.*;

import com.jaunt.UserAgent;

import java.io.*;

/**
 * Scrapes "The Blue Alliance" to retrieve important information.
 * @author Finn Frankis
 * @version 6/11/18
 */
public class TBAScraper
{
    private static ArrayList<Team> teams = new ArrayList<Team>();
    private static int NUM_TEAMS = 7332;
    private static int ITERATIONS = NUM_TEAMS / 50;
    public static void main (String[] args) throws Exception
    {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int j = 0; j < ITERATIONS; j++)
        {
            for (int i = j * NUM_TEAMS/ITERATIONS; i < (j+1) * NUM_TEAMS/ITERATIONS; i++)
            {
                TeamGetter tg = new TeamGetter(i);
                tg.start();
                threads.add(tg);
            }
        
            for (Thread t : threads)
                t.join();
        }
        
        teams.sort(new TeamComparator());
        PrintWriter pw = new PrintWriter("teams.json");
        pw.println("{");
        pw.println("\t\"teams\": [");
        for(int a = 0; a < teams.size(); a++) {
                Team te = teams.get(a);
                pw.println(addTabs(2) + te.getNumber() + ": {");
                pw.println(addTabs(3) + "\"name\": \"" + te.getName() + "\",");
                pw.println(addTabs(3) + "\"rookie-year\": " + te.getRookieYear() + ",");
                pw.println(addTabs(3) + "\"active\": " + te.isActive() + ",");
                ArrayList<Integer> activeYears = te.getActiveYears();
                for (int year : activeYears)
                {
                    pw.println(addTabs(3) + "" + year + ": [");
                    if (te.getTotalMatchWins().containsKey(year))
                        pw.println(addTabs(4) + "\"match-wins\": " + te.getTotalMatchWins().get(year) + ",");
                    if (te.getTotalMatchLosses().containsKey(year))
                        pw.println(addTabs(4) + "\"match-losses\": " + te.getTotalMatchLosses().get(year) + ",");
                    if (te.getTotalMatchTies().containsKey(year))
                        pw.println(addTabs(4) + "\"match-ties\": " + te.getTotalMatchTies().get(year) + ",");
                    if (te.getNumCompetitions().containsKey(year))
                        pw.println(addTabs(4) + "\"competitions\": " + te.getNumCompetitions().get(year) + ",");
                    if (te.getNumWins().containsKey(year))
                        pw.println(addTabs(4) + "\"competition-wins\": " + te.getNumWins().get(year) + ",");
                    if (te.getNumChairmanWins().containsKey(year))
                        pw.println(addTabs(4) + "\"chairman-wins\": " + te.getNumChairmanWins().get(year) + ",");
                    pw.println(addTabs(3) + "]");
                }
                
                if(a + 1 < teams.size())
                    pw.println(addTabs(2) + "},");
                else
                    pw.println(addTabs(2) + "}");
            }
            pw.println(addTabs(1) + "]");
            pw.println("}");
            pw.close();
            System.out.println("wrote successfully");
        }
        


    
    private static String addTabs(int indentation)
    {
        if (indentation == 0)
            return "";
        return "\t" + addTabs(indentation-1);
    }
    /**
     * Gets a given team's name given its TBA HTML page.
     * @param document the HTML page
     * @return the team's name
     */
    private static String getTeamName(String document)
    {
        String name = document.substring(document.indexOf("<title>") + "<title>".length(), document.indexOf(" -"));
        if (name.indexOf("(History") >= 0)
            name = name.substring(0, name.indexOf("(") - 1);
        return name;
    }
    
    /**
     * Gets the number of matches the team has won, tied, and lost for a given year.
     * @param document the HTML page
     * @return the team's wins, ties, and losses in the format [wins, losses, ties]
     * @throws Exception if this page is not year-specific
     */
    private static int[] getTeamMatchPlay(String document) throws Exception
    {
        int year = getYear(document);
        int wins = 0;
        int ties = 0; 
        int losses = 0;
        // for 2015, average the qual scores and playoff scores for the value; store integer part in wins, first decimal digit in losses, second decimal digit in ties
        if (year == 2015)
        {
            if (document.indexOf("qual score of <strong>") < 0)
            {
                return new int[] {-1, 0, 0};
            }
            document = document.substring(document.indexOf("qual score of <strong>") + "qual score of <strong>".length());
            double avgQualScore = Double.parseDouble(document.substring(0, document.indexOf("</strong>")));
            document = document.substring(document.indexOf("</strong>") + "</strong>".length());
            double avgPlayoffScore;
            if (document.indexOf("playoff score of <strong>") >= 0)
                avgPlayoffScore = Double.parseDouble(document.substring(document.indexOf("playoff score of <strong>") + "playoff score of <strong>".length(), document.indexOf("</strong>")));
            else
                avgPlayoffScore = avgQualScore;
            double avg = (avgQualScore + avgPlayoffScore)/2;
            wins = (int) avg;
            losses = Integer.parseInt((avg + "").substring((avg + "").indexOf(".") + 1, (avg + "").indexOf(".") + 2));
            if ((avg + "").substring((avg + "").indexOf(".") + 1).length() > 1)
                ties = Integer.parseInt((avg + "").substring((avg + "").indexOf(".") + 2, (avg + "").indexOf(".") + 3));
        }
        else if (year != -1)
        {
            int index = document.indexOf("h2 id=\"event-results");
            document = document.substring(index);
            index = document.indexOf("<strong>");
            document = document.substring(index + "<strong>".length());
            document = document.substring(0, document.indexOf("</strong>"));
            if (document.indexOf("-") >= 0)
            {
                wins = Integer.parseInt(document.substring(0, document.indexOf("-")));
                document = document.substring(document.indexOf("-") + 1);
                losses = Integer.parseInt(document.substring(0, document.indexOf("-")));
                document = document.substring(document.indexOf("-") + 1);
                ties = Integer.parseInt(document);
            }
            else
                return new int[] {-1, -1, -1};
            
        }
        else
        {
            throw new Exception("This page is not year-specific");
        }
        return new int[] {wins, losses, ties};
    }
    
    /**
     * Gets this team's rookie year.
     * @param document the HTML page
     * @return the team's rookie year
     * @throws Exception 
     */
    private static int getRookieYear (String document) throws Exception
    {
        int min = Integer.MAX_VALUE;
        for (int i : getActiveYears(document))
        {
            if (i < min)
                min = i;
        }
        return min;
    }
    
    /**
     * Determines whether this team is active.
     * @param document the HTML file
     * @return true if this team is active; false otherwise
     */
    private static boolean isActive(String document)
    {
        return document.indexOf("Last competed in") < 0;
    }
    
    /**
     * Gets the years where this team has been active
     * @param document the HTML file
     * @return the number of years where this team has been active
     */
    private static ArrayList<Integer> getActiveYears (String document)
    {
        ArrayList<Integer> years = new ArrayList<Integer>();
        document = document.substring(document.indexOf("dropdown-menu tba-dropdown-menu-limited")); 
        document = document.substring(0, document.indexOf("</ul>"));
        while (document.contains("<li>"))
        {
            document = document.substring(document.indexOf("<a"));
            document = document.substring(document.indexOf(">") + ">".length());
            String tempDocument = document.substring(0, document.indexOf("<"));
            if (!document.contains("H"))
            {
                years.add(Integer.parseInt(tempDocument.substring(0, tempDocument.indexOf(" Season"))));
            }
            
            document = document.substring(document.indexOf("<"));
        }
        return years;
    }
    
    /**
     * Gets the number of times this team has won a competition or won Chairman's in a year.
     * @param document the HTML file
     * @return the number of wins this team has attained in the format [wins, chairman's]
     * @throws Exception if the given document is not year-specific
     */
    private static int[] getNumWins (String document) throws Exception
    {
        int wins = 0;
        int chairmans = 0;
        if (getYear(document) != -1)
        {
            while (document.indexOf("banner") >= 0)
            {
                document = document.substring(document.indexOf("banner"));
                String tempDocument = document.substring(0, document.indexOf("</div"));
                if (tempDocument.contains("Winner"))
                    wins++;
                else if (tempDocument.contains("Chairman"))
                    chairmans++;
                document = document.substring(document.indexOf("</div>"));
            }
            return new int[] {wins, chairmans};
        }
        throw new Exception("The page must refer to a specific year");
        
    }
    
    /**
     * Gets the year to which this page refers.
     * @param document the HTML file
     * @return the year which this page refers to; -1 if there isn't a specific year
     */
    private static int getYear(String document)
    {
        document = document.substring(document.indexOf("btn btn-default btn-lg dropdown-toggle"));
        document = document.substring(0, document.indexOf("<span"));
        if (document.indexOf("History") >= 0)
            return -1;
        document = document.substring(document.indexOf("Season") - "Season".length()+1, document.indexOf(" Season"));
        return Integer.parseInt(document);
    }

    
    /**
     * Gets how many competitions a team has been to in a given year.
     * @param document the HTML file
     * @return the number of competitions
     * @throws Exception if the given document is not year-specific
     */
    private static int getNumCompetitions(String document, int teamNumber) throws Exception
    {
        if (getYear(document) != -1)
        {
            document = document.substring(document.indexOf("#event-results"));
            document = document.substring(0, document.indexOf("</ul>"));
            return (document.length() - document.replace("<li>", "").length()) / 4;
        }
        throw new Exception("This document is not year-specific.");
    }
    
    static class TeamGetter extends Thread
    {
        int i;
        /**
         * Constructs a new TeamGetter object.
         * @param the number of the team to be scraped
         */
        public TeamGetter (int num)
        {
            i = num;
        }
        
        @Override
        public void run()
        {
            int year = 0;
            try
            {
                UserAgent ua = new UserAgent();
                ua.visit("https://thebluealliance.com/team/" + i);
                
                String document = ua.doc.innerHTML();
                String name = getTeamName(document);
                int rookieYear = getRookieYear(document);
                ArrayList<Integer> years = getActiveYears(document);
                Team t = new Team(getTeamName(document), i, rookieYear, new TreeMap<Integer, Double>(), new TreeMap<Integer, Integer>(), new TreeMap<Integer, Integer>(), new HashMap<Integer, Integer>(), 
                        new TreeMap<Integer, Integer>(), new TreeMap<Integer, Integer>(), isActive(document), years);
                for (int b = 0; b < years.size(); b++)
                {
                    year = years.get(b);
                    ua.visit("https://thebluealliance.com/team/" + i + "/" + year);
                    String yearDocument = ua.doc.innerHTML();
                    if (year >= 2002)
                    {
                        int[] matchPlay = getTeamMatchPlay(yearDocument);
                        if (matchPlay[0] >= 0 && matchPlay[1] >= 0 && matchPlay[2] >=0)
                        {
                            if (year != 2015)
                            {
                                t.getTotalMatchWins().put(year, new Double(matchPlay[0]));
                                t.getTotalMatchLosses().put(year, new Integer(matchPlay[1]));
                                t.getTotalMatchTies().put(year, new Integer(matchPlay[2]));
                            }
                            else
                            {
                                t.getTotalMatchWins().put(year, Double.parseDouble(matchPlay[0] + "." + matchPlay[1] + "" + matchPlay[2]));
                            }
                        }
                    }
                    
                    
                    
                    int[] regionalWins = getNumWins(yearDocument);
                    t.getNumWins().put(year, regionalWins[0]);
                    t.getNumChairmanWins().put(year, regionalWins[1]);
                    
                    t.getNumCompetitions().put(year, getNumCompetitions(yearDocument, i));
                }
                System.out.println(t);
                teams.add(t);
            }
            catch  (Exception e)
            {
                if (e.getLocalizedMessage()!= null && e.getLocalizedMessage().indexOf("UserAgent") >= 0)
                {
                    System.out.println(e.getLocalizedMessage() + ": team " + i + " doesn't exist");
                }
                else
                {
                    System.out.println("team " + i + " had a problem in " + year);
                    e.printStackTrace();
                }
            }
        }
    }
}
