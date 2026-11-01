package football;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ============================================================
// OOP CONCEPT: ENCAPSULATION
// Match encapsulates all state associated with an individual fixture:
// team references, scores, status, round metadata, and assigned referee.
// ============================================================
public class Match implements Serializable {

    private static final long serialVersionUID = 6L;

    public enum Status { SCHEDULED, COMPLETED }

    // --------------------------------------------------------
    // OOP CONCEPT: ENCAPSULATION — private fields
    // --------------------------------------------------------
    private String   matchId;
    private String   homeTeamId;
    private String   awayTeamId;
    private String   homeTeamName;
    private String   awayTeamName;
    private int      homeScore;
    private int      awayScore;
    private Status   status;
    private int      round;
    private String   roundLabel;
    private String   scheduledTime;

    // Association with Referee (extending Person)
    private Referee  referee;

    // Knockout progression links
    private String   nextMatchId;
    private boolean  isHomeForNext;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    public Match(String matchId, String homeTeamId, String homeTeamName,
                 String awayTeamId, String awayTeamName, int round, String roundLabel) {
        this.matchId       = matchId;
        this.homeTeamId    = homeTeamId;
        this.homeTeamName  = homeTeamName;
        this.awayTeamId    = awayTeamId;
        this.awayTeamName  = awayTeamName;
        this.homeScore     = 0;
        this.awayScore     = 0;
        this.status        = Status.SCHEDULED;
        this.round         = round;
        this.roundLabel    = roundLabel;
        this.scheduledTime = LocalDateTime.now().format(FMT);
        this.referee       = null;
        this.nextMatchId   = null;
        this.isHomeForNext = true;
    }

    // --- Getters (Encapsulation) ---
    public String  getMatchId()       { return matchId;       }
    public String  getHomeTeamId()    { return homeTeamId;    }
    public String  getAwayTeamId()    { return awayTeamId;    }
    public String  getHomeTeamName()  { return homeTeamName;  }
    public String  getAwayTeamName()  { return awayTeamName;  }
    public int     getHomeScore()     { return homeScore;     }
    public int     getAwayScore()     { return awayScore;     }
    public Status  getStatus()        { return status;        }
    public int     getRound()         { return round;         }
    public String  getRoundLabel()    { return roundLabel;    }
    public String  getScheduledTime() { return scheduledTime; }
    public Referee getReferee()       { return referee;       }
    public String  getNextMatchId()   { return nextMatchId;   }
    public boolean isHomeForNext()    { return isHomeForNext; }

    // --- Setters (Encapsulation) ---
    public void setHomeTeamId(String homeTeamId)     { this.homeTeamId = homeTeamId; }
    public void setAwayTeamId(String awayTeamId)     { this.awayTeamId = awayTeamId; }
    public void setHomeTeamName(String homeTeamName) { this.homeTeamName = homeTeamName; }
    public void setAwayTeamName(String awayTeamName) { this.awayTeamName = awayTeamName; }
    public void setReferee(Referee referee)           { this.referee = referee; }
    public void setNextMatchId(String nextMatchId)   { this.nextMatchId = nextMatchId; }
    public void setHomeForNext(boolean homeForNext)  { this.isHomeForNext = homeForNext; }

    // Record verified score
    public void recordResult(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status    = Status.COMPLETED;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public String getOutcome() {
        if (!isCompleted()) return "Not Played";
        if (homeScore > awayScore) return homeTeamName + " WIN";
        if (awayScore > homeScore) return awayTeamName + " WIN";
        return "DRAW";
    }

    public String getWinnerTeamId() {
        if (!isCompleted()) return null;
        if (homeScore > awayScore) return homeTeamId;
        if (awayScore > homeScore) return awayTeamId;
        return null; // Tie
    }

    public String getWinnerTeamName() {
        if (!isCompleted()) return null;
        if (homeScore > awayScore) return homeTeamName;
        if (awayScore > homeScore) return awayTeamName;
        return "Tie";
    }

    public void display() {
        String label = (roundLabel != null && !roundLabel.isEmpty()) ? roundLabel : ("Round " + round);
        String refText = (referee != null) ? " (Ref: " + referee.getName() + ")" : "";

        if (isCompleted()) {
            System.out.printf("  [%-14s] %-8s : %-18s %2d - %-2d %-18s  --> %-18s%s%n",
                    label, matchId, homeTeamName, homeScore, awayScore, awayTeamName, getOutcome(), refText);
        } else {
            System.out.printf("  [%-14s] %-8s : %-18s    vs    %-18s  --> [SCHEDULED]%s%n",
                    label, matchId, homeTeamName, awayTeamName, refText);
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s vs %s (%s)",
                matchId, roundLabel, homeTeamName, awayTeamName,
                isCompleted() ? (homeScore + "-" + awayScore) : "SCHEDULED");
    }
}
