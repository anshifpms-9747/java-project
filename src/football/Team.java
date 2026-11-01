package football;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// ============================================================
// OOP CONCEPT: ENCAPSULATION
// Team bundles state (id, name, city, squad, statistics) and exposes
// only controlled operations through public methods.
//
// OOP CONCEPT: COLLECTIONS
// Uses java.util.ArrayList to dynamically manage the squad of Player objects.
// ============================================================
public class Team implements Serializable {

    private static final long serialVersionUID = 5L;

    // --------------------------------------------------------
    // OOP CONCEPT: ENCAPSULATION — private fields
    // --------------------------------------------------------
    private String id;
    private String name;
    private String city;
    private Coach  coach;

    // OOP CONCEPT: COLLECTIONS — ArrayList of Player objects
    private List<Player> players;

    // Points table statistics
    private int matchesPlayed;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int points;

    public Team(String id, String name, String city) {
        this.id            = id;
        this.name          = name;
        this.city          = city;
        this.players       = new ArrayList<>();
        this.matchesPlayed = 0;
        this.wins          = 0;
        this.draws         = 0;
        this.losses        = 0;
        this.goalsFor      = 0;
        this.goalsAgainst  = 0;
        this.points        = 0;
    }

    // --- Getters (Encapsulation) ---
    public String       getId()             { return id;            }
    public String       getName()           { return name;          }
    public String       getCity()           { return city;          }
    public Coach        getCoach()          { return coach;         }
    public List<Player> getPlayers()        { return players;       }
    public int          getMatchesPlayed()  { return matchesPlayed; }
    public int          getWins()           { return wins;          }
    public int          getDraws()          { return draws;         }
    public int          getLosses()         { return losses;        }
    public int          getGoalsFor()       { return goalsFor;      }
    public int          getGoalsAgainst()   { return goalsAgainst;  }
    public int          getPoints()         { return points;        }
    public int          getGoalDifference() { return goalsFor - goalsAgainst; }

    // --- Setters (Encapsulation) ---
    public void setId(String id)       { this.id = id;       }
    public void setName(String name)   { this.name = name;   }
    public void setCity(String city)   { this.city = city;   }
    public void setCoach(Coach coach)  { this.coach = coach; }

    // --- Squad management (Collections) ---
    public void addPlayer(Player p) {
        players.add(p);
    }

    public boolean removePlayer(String playerId) {
        return players.removeIf(p -> p.getId().equalsIgnoreCase(playerId));
    }

    /**
     * Find a player by jersey number.
     */
    public Player findPlayerByJersey(int jersey) {
        for (Player p : players) {
            if (p.getJerseyNumber() == jersey) {
                return p;
            }
        }
        return null;
    }

    // --- Stats calculation (Win=3, Draw=1, Loss=0) ---
    public void recordResult(int scored, int conceded) {
        matchesPlayed++;
        goalsFor     += scored;
        goalsAgainst += conceded;

        if (scored > conceded) {
            wins++;
            points += 3; // Win = 3 points
        } else if (scored == conceded) {
            draws++;
            points += 1; // Draw = 1 point
        } else {
            losses++;
            // Loss = 0 points
        }
    }

    /**
     * Resets performance metrics (used when re-generating fixtures).
     */
    public void resetStats() {
        matchesPlayed = 0;
        wins          = 0;
        draws         = 0;
        losses        = 0;
        goalsFor      = 0;
        goalsAgainst  = 0;
        points        = 0;
    }

    public void displaySummary() {
        System.out.println("  +-----------------------------------------------------------+");
        System.out.printf ("  | Team ID : %-48s|%n", id);
        System.out.printf ("  | Name    : %-48s|%n", name);
        System.out.printf ("  | City    : %-48s|%n", city);
        if (coach != null) {
            System.out.printf ("  | Coach   : %-48s|%n", coach.getName() + " (" + coach.getNationality() + ")");
        } else {
            System.out.printf ("  | Coach   : %-48s|%n", "[None Assigned]");
        }
        System.out.printf ("  | Squad   : %-48s|%n", players.size() + " Players registered");
        System.out.println("  +-----------------------------------------------------------+");
    }

    public void displayFullDetails() {
        displaySummary();
        if (coach != null) {
            System.out.println("\n  >> Coaching Staff:");
            coach.displayInfo(); // Polymorphic call to Person.displayInfo()
        }
        System.out.println("\n  >> Squad Roster:");
        if (players.isEmpty()) {
            System.out.println("     (No players currently registered in squad)");
        } else {
            for (Player p : players) {
                p.displayInfo(); // Polymorphic call to Person.displayInfo()
            }
        }
        System.out.println("\n  >> Tournament Statistics:");
        System.out.printf("     Played: %d | Won: %d | Drawn: %d | Lost: %d%n",
                matchesPlayed, wins, draws, losses);
        System.out.printf("     Goals For: %d | Goals Against: %d | Goal Diff: %+d | Points: %d%n",
                goalsFor, goalsAgainst, getGoalDifference(), points);
    }

    @Override
    public String toString() {
        return String.format("%-6s | %-22s | %-15s", id, name, city);
    }
}
